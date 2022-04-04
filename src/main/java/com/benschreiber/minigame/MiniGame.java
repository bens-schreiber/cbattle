package com.benschreiber.minigame;

import com.benschreiber.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class MiniGame {

    public static final Location worldSpawnLocation = new Location(Bukkit.getWorld("minigame"), 190.5, 10.5, -136.5);

    protected Party party;
    public abstract void start();
    public abstract void prematureEnd();
    public abstract void respawn(Player player);
    public abstract void updateScoreBoard(Player player);
}
