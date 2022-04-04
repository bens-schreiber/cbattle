package com.benschreiber.ui;


import com.benschreiber.minigame.CaveBattle;
import com.benschreiber.party.PartyManager;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.itemstackbuilder.ISBuilder;
import com.benschreiber.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

// UI Menu
public class MainMenu extends Menu {

    @Override
    public Inventory GUI(Player player) {

        // Create the inventory UI
        int rows = 9;
        int slot = 3;
        inventory = Bukkit.createInventory(null, rows, Utils.chat("&4&lCave Battle"));

        inventory.setItem(slot++,
                new ISBuilder(Material.CHEST)
                        .displayName("&5Party Up")
                        .loreString("&7Create and invite players to your party.")
                        .build());

        inventory.setItem(slot++,
                new ISBuilder(Material.DIAMOND_PICKAXE)
                        .displayName("&2Start")
                        .loreString("&7Begin the game with everyone in your party!")
                        .build());

        inventory.setItem(slot,
                new ISBuilder(Material.BARRIER)
                        .displayName("&cLeave")
                        .loreString("&7Leave the menu.")
                        .build());

        for (int i = 0; i < rows; i++) {
            try {
                inventory.setItem(inventory.firstEmpty(), new ISBuilder(Material.WHITE_STAINED_GLASS_PANE)
                        .displayName(" ")
                        .build());
            } catch (Exception e) {
                break;
            }
        }

        return inventory;
    }

    @Override
    public void clicked(Player player, int slot, ItemStack clicked, Inventory inventory) {

        if (PermissionManager.hasPermission(player, Permission.ALL_PARTY_COMMANDS)) {

            if (clicked.getItemMeta() == null) return;
            String title = clicked.getItemMeta().getDisplayName();

            // Party menu
            if (title.equalsIgnoreCase(Utils.chat("&5Party Up"))) {
                player.closeInventory();
                player.openInventory(new PartyUp().GUI(player));
            }

            // Leave button
            else if (title.equalsIgnoreCase(Utils.chat("&cLeave"))) {
                player.closeInventory();
            }

            // Start game button
            else if (title.equalsIgnoreCase(Utils.chat("&2Start"))) {
                if (PartyManager.isAPartyLeader(player)) {

                    if (PartyManager.getParty(player).getPlayers().size() > 1) {

                        if (PartyManager.gameIsRunning()) {

                            player.sendMessage(Utils.chat("&cA game is currently running :("));

                        } else {
                            player.closeInventory();
                            PartyManager.getParty(player).broadcast("The game is starting.");
                        }
                        new CaveBattle(PartyManager.getParty(player)).start();
                    } else {
                        player.sendMessage(Utils.chat("&cYou don't have enough people in your party to do this :("));
                    }
                } else {
                    player.sendMessage(Utils.chat("&cYou need to be a party leader to do this."));
                }
            }
        } else {
            player.sendMessage(Utils.chat("&cYou can't do that right now :("));
        }
    }
}
