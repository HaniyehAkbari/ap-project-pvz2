package com.pvz2.models.network.onlineIZombie;

import com.pvz2.models.network.ClientHandler;
import com.pvz2.models.network.onlineIZombie.messages.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MatchManager {

    private static class PendingInvite {
        final ClientHandler challenger;
        final ClientHandler target;
        PendingInvite(ClientHandler challenger, ClientHandler target) {
            this.challenger = challenger;
            this.target = target;
        }
    }

    private final Map<String, PendingInvite> pendingInvites = new ConcurrentHashMap<>();
    private final Map<String, Match> matchesById = new ConcurrentHashMap<>();
    private final Map<ClientHandler, Match> matchByPlayer = new ConcurrentHashMap<>();
    private final Consumer<Match> onMatchCreated;

    private ClientHandler waitingForRandom;

    public MatchManager(Consumer<Match> onMatchCreated) {
        this.onMatchCreated = onMatchCreated;
    }


    public synchronized ChallengeResponse challenge(ClientHandler challenger, ClientHandler target) {
        ChallengeResponse res = new ChallengeResponse();

        if (target == null) {
            res.success = false;
            res.errorMessage = "That user is invalid or offline.";
            return res;
        }
        if (target == challenger) {
            res.success = false;
            res.errorMessage = "You can't challenge yourself.";
            return res;
        }
        if (isBusy(challenger)) {
            res.success = false;
            res.errorMessage = "You're already queued or in a match.";
            return res;
        }
        if (isBusy(target)) {
            res.success = false;
            res.errorMessage = "That player is already queued or in a match.";
            return res;
        }

        String inviteId = UUID.randomUUID().toString();
        pendingInvites.put(inviteId, new PendingInvite(challenger, target));

        target.send("CHALLENGE_INVITE", null, new ChallengeInvite(inviteId, challenger.getUsername()));

        res.success = true;
        res.inviteId = inviteId;
        return res;
    }

    public synchronized ChallengeAnswerResponse answerChallenge(ClientHandler responder,
                                                                String inviteId, boolean accept) {
        PendingInvite invite = pendingInvites.get(inviteId);

        if (invite == null || invite.target != responder) {
            return new ChallengeAnswerResponse(false, "This invite is no longer valid.");
        }
        pendingInvites.remove(inviteId);

        if (!accept) {
            invite.challenger.send("CHALLENGE_DECLINED", null, new ChallengeDeclined(responder.getUsername()));
            return new ChallengeAnswerResponse(true, null);
        }

        if (isBusy(invite.challenger)) {
            return new ChallengeAnswerResponse(false, "The challenger is no longer available.");
        }

        createMatch(invite.challenger, invite.target);
        return new ChallengeAnswerResponse(true, null);
    }

    public synchronized RandomMatchResponse joinRandomQueue(ClientHandler player) {
        RandomMatchResponse res = new RandomMatchResponse();

        if (isBusy(player)) {
            res.success = false;
            res.errorMessage = "You're already queued or in a match.";
            return res;
        }

        if (waitingForRandom == null) {
            waitingForRandom = player;
            res.success = true;
            res.waiting = true;
            return res;
        }

        ClientHandler opponent = waitingForRandom;
        waitingForRandom = null;

        createMatch(player, opponent);
        res.success = true;
        res.waiting = false;
        return res;
    }

    public synchronized void cancelRandomQueue(ClientHandler player) {
        if (waitingForRandom == player) {
            waitingForRandom = null;
        }
    }

    private void createMatch(ClientHandler a, ClientHandler b) {
        Match match = new Match(a, b);
        matchesById.put(match.getMatchId(), match);
        matchByPlayer.put(a, match);
        matchByPlayer.put(b, match);

        a.send("MATCH_FOUND", null, new MatchFound(
            match.getMatchId(), b.getUsername(), match.getSideOf(a).name()));
        b.send("MATCH_FOUND", null, new MatchFound(
            match.getMatchId(), a.getUsername(), match.getSideOf(b).name()));

        onMatchCreated.accept(match);
    }

    private boolean isBusy(ClientHandler player) {
        return player == waitingForRandom || matchByPlayer.containsKey(player);
    }

    public Match getMatchOf(ClientHandler player) {
        return matchByPlayer.get(player);
    }

    public synchronized void endMatch(String matchId) {
        Match match = matchesById.remove(matchId);
        if (match != null) {
            matchByPlayer.remove(match.getPlantsPlayer());
            matchByPlayer.remove(match.getZombiesPlayer());
        }
    }

    public synchronized void handleDisconnect(ClientHandler player) {
        cancelRandomQueue(player);
        pendingInvites.entrySet().removeIf(e ->
            e.getValue().challenger == player || e.getValue().target == player);

        Match match = matchByPlayer.remove(player);
        if (match != null) {
            matchesById.remove(match.getMatchId());
            ClientHandler opponent = match.getOpponentOf(player);
            if (opponent != null) {
                matchByPlayer.remove(opponent);
            }
        }
    }
}
