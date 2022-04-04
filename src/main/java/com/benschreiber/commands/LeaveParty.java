package com.benschreiber.commands;

import com.benschreiber.Main;
import com.benschreiber.party.Party;
import com.benschreiber.party.PartyManager;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveParty implements CommandExecutor {

    public LeaveParty (Main plugin) {
        plugin.getCommand("leave").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = ((Player) commandSender).getPlayer();

        if (PermissionManager.hasPermission(player, Permission.ALL_PARTY_COMMANDS)) {

            if (PartyManager.isAPartyLeader(player)) {
                PartyManager.getParty(player).broadcast("Party disbanded.");
                PartyManager.disbandParty(player);
            } else if (PartyManager.inAParty(player)) {

                Party party = PartyManager.getParty(player);
                PartyManager.removePartyMember(player);
                party.broadcast(player.getDisplayName() + " has left your party. Loser lol");
                player.sendMessage(Utils.formatMsg("You have left the party."));
            }
        }

        return false;
    }
}