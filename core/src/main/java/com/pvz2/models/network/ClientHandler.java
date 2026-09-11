package com.pvz2.models.network;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {
    private static final Gson GSON = NetworkGson.INSTANCE;

    private final Socket socket;
    private final GameServer server;
    private BufferedReader in;
    private PrintWriter out;
    private volatile String username;

    private final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>(200);
    private Thread senderThread;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            startSenderThread();

            String line;
            while ((line = in.readLine()) != null) {
                try {
                    NetworkMessage msg = GSON.fromJson(line, NetworkMessage.class);
                    if (msg == null || msg.type == null) continue;
                    server.dispatch(this, msg);
                } catch (Exception e) {
                    System.err.println("Malformed message from " +
                        (username != null ? username : socket.getRemoteSocketAddress()) + ": " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection lost: " +
                (username != null ? username : socket.getRemoteSocketAddress()));
        } finally {
            disconnect();
        }
    }

    public void send(String type, String requestId, Object payload) {
        if (!running) return;

        NetworkMessage msg = new NetworkMessage(type, GSON.toJsonTree(payload));
        msg.requestId = requestId;
        String line = GSON.toJson(msg);

        if (!sendQueue.offer(line)) {
            sendQueue.poll();
            sendQueue.offer(line);
        }
    }

    private void sendLoop() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                String line = sendQueue.take();
                if (out != null) {
                    out.println(line);
                }
            }
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            System.err.println("Error sending to " + (username != null ? username : socket.getRemoteSocketAddress()));
        }
    }

    private void startSenderThread() {
        senderThread = new Thread(this::sendLoop, "Sender-" +
            (username != null ? username : socket.getRemoteSocketAddress()));
        senderThread.setDaemon(true);
        senderThread.start();
    }

    private void disconnect() {
        running = false;
        if (senderThread != null) {
            senderThread.interrupt();
        }
        server.onDisconnect(this);
        try { socket.close(); } catch (IOException ignored) {}
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
