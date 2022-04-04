package com.benschreiber.listeners;

import com.benschreiber.Main;

import com.benschreiber.ui.*;
import com.benschreiber.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    public InventoryClickListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    // On an inventory space clicked
    public void onClick(InventoryClickEvent event) {

            Menu menu = null;
            String title = event.getView().getTitle();

            if (title.equals(Utils.chat("&4&lCave Battle"))) {
                menu = new MainMenu();
            } else if (title.equals(Utils.chat("&4&lParty Up"))) {
                menu = new PartyUp();
            } else if (title.equals(Utils.chat("&4&lInvite Players"))) {
                menu = new InvitePlayers();
            } else if (title.equals(Utils.chat("&d&lParty Members"))) {
                menu = new PartyMembers();
            }

            if (menu == null || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            event.setCancelled(true);
            menu.clicked((Player) event.getWhoClicked(), event.getSlot(), event.getCurrentItem(), event.getInventory());
    }

}
