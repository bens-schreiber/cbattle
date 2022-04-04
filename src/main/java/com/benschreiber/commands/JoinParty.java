package com.benschreiber.commands;

import com.benschreiber.Main;
import com.benschreiber.party.Party;
import com.benschreiber.party.PartyManager;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinParty implements CommandExecutor {

    public JoinParty (Main plugin) {
        plugin.getCommand("_join").setExecutor(this);
    }

    @Override
    // Join command
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = ((Player) commandSender).getPlayer();

        if (PermissionManager.hasPermission(player, Permission.JOIN_PARTY)) {

            //Get the leader display name from the command
            Player leader = Bukkit.getServer().getPlayer(strings[0]);

            //If the user is not already in a party
            if (!PartyManager.inAParty(player)) {

                //If the party exists
                if (PartyManager.isAPartyLeader(leader)) {

                    if (PartyManager.getParty(leader).getPlayers().size() < 8) {

                        player.sendMessage(Utils.formatMsg("You have joined the party."));

                        Party party = PartyManager.getParty(leader);

                        party.broadcast(player.getDisplayName() + " has joined your party.");

                        PartyManager.addPartyMember(party.getId(), player);

                    } else {
                        player.sendMessage(Utils.chat("&cToo many people in party :("));
                    }
                } else {
                    player.sendMessage(Utils.chat("&cParty does not exist :("));
                }
            } else {
                player.sendMessage(Utils.chat("&cYou must leave your party first before joining a new one. (/leave)"));
            }
        } else {
            player.sendMessage(Utils.chat("&cInvite expired :("));
        }



        return false;
        }
}