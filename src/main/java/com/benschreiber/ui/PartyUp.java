package com.benschreiber.ui;


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
public class PartyUp extends Menu {


    @Override
    public Inventory GUI(Player player) {

        int rows = 9;
        int slot = 2;

        // Create the inventory UI
        inventory = Bukkit.createInventory(null, rows, Utils.chat("&4&lParty Up"));

        if (PartyManager.inAParty(player)) {
            inventory.setItem(slot++, new ISBuilder(Material.BOOKSHELF)
                    .displayName("&dParty Members")
                    .loreString("&7Show my party members")
                    .build()
            );


            if (PartyManager.isAPartyLeader(player)) {
                inventory.setItem(slot++, new ISBuilder(Material.ENDER_CHEST)
                        .displayName("&aInvite Players")
                        .loreString("&7Invite players to your party.")
                        .build());
            }

            inventory.setItem(slot++, new ISBuilder(Material.REDSTONE_BLOCK)
                    .displayName("&4Leave Party")
                    .loreString("&7Leave your party or disband as party leader")
                    .build());


        } else {
            slot = 3;
            inventory.setItem(slot++, new ISBuilder(Material.CRAFTING_TABLE)
                    .displayName("&5Create a Party")
                    .loreString("&7Create a party for your friends to join")
                    .build());
        }

        inventory.setItem(slot++, new ISBuilder(Material.DIAMOND)
                .displayName("&bReturn")
                .loreString("&7Return to the main menu")
                .build());

        inventory.setItem(slot, new ISBuilder(Material.BARRIER)
                .displayName("&cClose")
                .build());


        // lol
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

            // Create party button
            if (title.equalsIgnoreCase(Utils.chat("&5Create a Party"))) {

                // Create a new party if player is not a party leader
                if (PartyManager.isAPartyLeader(player)) {
                    player.sendMessage(Utils.chat("&cDisband your current party to make a new one."));
                } else if (PartyManager.inAParty(player)) {
                    player.sendMessage(Utils.chat("&cLeave your current party to make a new one."));
                } else {
                    PartyManager.newParty(player);
                    player.sendMessage(Utils.formatMsg("Party created."));
                    player.closeInventory();
                    player.openInventory(new PartyUp().GUI(player));
                }
            }

            if (title.equalsIgnoreCase(Utils.chat("&dParty Members"))) {
                player.closeInventory();
                player.openInventory(new PartyMembers().GUI(player));
            }

            // Invite Players menu
            else if (title.equalsIgnoreCase(Utils.chat("&aInvite Players"))) {
                if (PartyManager.isAPartyLeader(player)) {
                    player.closeInventory();
                    player.openInventory(new InvitePlayers().GUI(player));
                } else {
                    player.sendMessage(Utils.chat("&cYou are not a party leader :("));
                }
            }

            // Return button
            else if (title.equalsIgnoreCase(Utils.chat("&bReturn"))) {
                player.closeInventory();
                player.openInventory(new MainMenu().GUI(player));
            }

            // Leave button
            else if (title.equalsIgnoreCase(Utils.chat("&4Leave Party"))) {
                player.performCommand("leave");
                player.openInventory(new MainMenu().GUI(player));
            }

            // Close button
            else if (title.equalsIgnoreCase(Utils.chat("&cClose"))) {
                player.closeInventory();
            }

        } else {
            player.sendMessage(Utils.chat("&cYou can't do that right now :("));
        }
    }

}
