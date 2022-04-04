package com.benschreiber.ui;


import com.benschreiber.Main;
import com.benschreiber.party.PartyManager;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.itemstackbuilder.ISBuilder;
import com.benschreiber.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

// UI Menu
public class InvitePlayers extends Menu {

    @Override
    public Inventory GUI(Player player) {

        int rows = 36;

        // Create the inventory UI
        inventory = Bukkit.createInventory(null, rows, Utils.chat("&4&lInvite Players"));

        // List all online players on the menu
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;
            inventory.addItem(new ISBuilder(Material.PLAYER_HEAD)
                    .playerHead(p)
                    .build());
        }

        inventory.addItem(new ISBuilder(Material.DIAMOND)
                .displayName("&bReturn")
                .loreString("&7Return to the My Party menu")
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

            // Invite buttons
            else {

                if (PartyManager.getParty(player).getPlayers().size() < 8) {

                    if (!PartyManager.getParty(player).getPlayers().contains(Utils.playerFromString(clicked.getItemMeta().getDisplayName()))) {

                        // Grab the player object from the player skull name
                        sendPendingInvite(player, Utils.playerFromString(clicked.getItemMeta().getDisplayName()));
                    }

                } else {
                    player.sendMessage(Utils.chat("&cToo many players in party :("));
                }
            }
        } else {
            player.sendMessage(Utils.chat("&cYou can't do that right now :("));
        }
    }

    private void sendPendingInvite(final Player leader, final Player invitee) {

        //Give player permission to use the join command
        PermissionManager.addPerm(invitee, Permission.JOIN_PARTY);

        TextComponent msg = new TextComponent(Utils.formatMsg("&n&oClick to join " + leader.getDisplayName() + "'s party. (10s)"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_join " + leader.getDisplayName()));

        // Send invite
        invitee.spigot().sendMessage(msg);

        // Confirm with player invite was sent
        leader.sendMessage(Utils.formatMsg("Invited " + invitee.getDisplayName()));

        // Pending invite, expires in 10s
        new BukkitRunnable() {

            @Override
            public void run() {
                PermissionManager.removePerm(invitee, Permission.JOIN_PARTY);
            }
        }.runTaskLater(Main.getInstance(), 200);


    }

}
