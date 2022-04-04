package com.benschreiber.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Utils {

    // Interprets minecraft chat color formatting in a string
    public static String chat(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Default prefix
    public static String formatMsg(String string) {
        return chat("&7[&aCave Battle&7] &c" + string);
    }

    /**
     * Get a player object from a players display name
     *
     * @param name the players exact name
     * @return player object
     */
    public static Player playerFromString(String name) {
        return Bukkit.getServer().getPlayer(ChatColor.stripColor(name));
    }

}
