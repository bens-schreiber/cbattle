package com.benschreiber.party;

import com.benschreiber.minigame.CaveBattle;
import com.benschreiber.utils.Utils;
import com.benschreiber.minigame.MiniGame;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Party {

    private Player leader;

    private final UUID id;

    private MiniGame miniGame = null;

    private final List<Player> players = new LinkedList<>();

    public Party(Player leader) {
        this.leader = leader;
        this.id = UUID.randomUUID();
        players.add(leader);
    }

    // Send a message to all members of a party
    public void broadcast(String message) {
        for (Player player : players) {
            player.sendMessage(Utils.formatMsg(message));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public UUID getId() {
        return id;
    }

    public boolean inMiniGame() {
        return !(miniGame == null);
    }

    public void setMiniGame(CaveBattle miniGame) {
        this.miniGame = miniGame;
    }

    public MiniGame getMiniGame() {
        return miniGame;
    }
}
