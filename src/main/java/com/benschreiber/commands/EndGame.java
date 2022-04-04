package com.benschreiber.commands;

import com.benschreiber.Main;
import com.benschreiber.party.PartyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class EndGame implements CommandExecutor {

    public EndGame(Main plugin) {

        plugin.getCommand("end").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        Player player = (Player) commandSender;
        if (PartyManager.isAPartyLeader(player) && PartyManager.inAMiniGame(player)) {

            PartyManager.getParty(player).getMiniGame().prematureEnd();

        }
        return false;
    }
}
