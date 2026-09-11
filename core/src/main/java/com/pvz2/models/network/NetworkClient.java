package com.pvz2.models.network;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

public class NetworkClient {
    private static final Gson GSON = NetworkGson.INSTANCE;
    private static NetworkClient instance;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final Map<String, SynchronousQueue<NetworkMessage>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String, Consumer<NetworkMessage>> pushHandlers = new ConcurrentHashMap<>();

    public static synchronized NetworkClient get() {
        if (instance == null) instance = new NetworkClient();
        return instance;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        Thread readerThread = new Thread(this::readLoop);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                NetworkMessage msg = GSON.fromJson(line, NetworkMessage.class);
                if (msg == null || msg.type == null) continue;

                if (msg.requestId != null && pendingRequests.containsKey(msg.requestId)) {
                    pendingRequests.get(msg.requestId).put(msg);
                } else {
                    Consumer<NetworkMessage> handler = pushHandlers.get(msg.type);
                    if (handler != null) handler.accept(msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Connection to server lost.");
            e.printStackTrace();
        }
    }

    public NetworkMessage sendRequest(String type, Object payload, long timeoutMs) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        SynchronousQueue<NetworkMessage> replyBox = new SynchronousQueue<>();
        pendingRequests.put(requestId, replyBox);

        NetworkMessage msg = new NetworkMessage(type, GSON.toJsonTree(payload));
        msg.requestId = requestId;
        out.println(GSON.toJson(msg));

        try {
            NetworkMessage reply = replyBox.poll(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (reply == null) throw new InterruptedException("Server did not respond in time.");
            return reply;
        } finally {
            pendingRequests.remove(requestId);
        }
    }

    public void onPush(String type, Consumer<NetworkMessage> handler) {
        pushHandlers.put(type, handler);
    }

    /**
     * Sends a message without waiting for any reply — for gameplay actions where the
     * server's response is a later GAME_STATE push rather than a direct answer.
     * Safe to call from the render thread, unlike sendRequest.
     */
    public void sendMessage(String type, Object payload) {
        NetworkMessage msg = new NetworkMessage(type, GSON.toJsonTree(payload));
        out.println(GSON.toJson(msg));
    }

    public <T> T parsePayload(NetworkMessage msg, Class<T> clazz) {
        return GSON.fromJson(msg.payload, clazz);
    }
}
