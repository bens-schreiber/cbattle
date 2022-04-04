package com.benschreiber.perms;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PermissionManager {

    private static final HashMap<UUID, List<Permission>> activePerms = new HashMap<>();

    public static boolean hasPermission(Player player, Permission permission) {
        return activePerms.containsKey(player.getUniqueId()) && activePerms.get(player.getUniqueId()).contains(permission);
    }

    public static List<Permission> getPlayersPerms(Player player) {
        return activePerms.get(player.getUniqueId());
    }

    public static void removePerm(Player player, Permission permission) {
        activePerms.get(player.getUniqueId()).remove(permission);
    }

    public static void addPerm(Player player, Permission... permissions) {
        for (Permission permission : permissions) {
            getPlayersPerms(player).add(permission);
        }
    }

    public static void addPlayer(Player player) {
        activePerms.put(player.getUniqueId(), new LinkedList<>());
    }

    public static void addPlayer(Player player, Permission... permissions) {
        addPlayer(player);
        addPerm(player, permissions);
    }

    public static void removePlayer(Player player) {
        activePerms.remove(player.getUniqueId());
    }

}
