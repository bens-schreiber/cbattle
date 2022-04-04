package com.benschreiber.ui;

import com.benschreiber.party.PartyManager;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.Utils;
import com.benschreiber.utils.itemstackbuilder.ISBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PartyMembers extends Menu {

    @Override
    public Inventory GUI(Player player) {

        inventory = Bukkit.createInventory(null, 36, Utils.chat("&d&lParty Members"));

        if (PartyManager.inAParty(player)) {
            for (Player p : PartyManager.getPartyMembers(player)) {
                inventory.addItem(new ISBuilder(Material.PLAYER_HEAD)
                        .playerHead(p)
                        .build()
                );
            }
        }

        inventory.addItem(new ISBuilder(Material.DIAMOND)
                .displayName("&bReturn")
                .loreString("&7Return to the main menu")
                .build());

        inventory.addItem(new ISBuilder(Material.BARRIER)
                .displayName("&cClose")
                .build());

        return inventory;
    }

    @Override
    public void clicked(Player player, int slot, ItemStack clicked, Inventory inventory) {

        if (PermissionManager.hasPermission(player, Permission.ALL_PARTY_COMMANDS)) {

            if (clicked.getItemMeta() == null) return;
            String title = clicked.getItemMeta().getDisplayName();

            // Return button
            if (title.equalsIgnoreCase(Utils.chat("&bReturn"))) {
                player.closeInventory();
                player.openInventory(new PartyUp().GUI(player));
            }

            // Close button
            else if (title.equalsIgnoreCase(Utils.chat("&cClose"))) {
                player.closeInventory();
            }
        }

    }
}
