package com.benschreiber.commands;

import com.benschreiber.Main;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.ui.MainMenu;
import com.benschreiber.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CBMenu implements CommandExecutor {

    public CBMenu(Main plugin) {

        plugin.getCommand("cb").setExecutor(this);
    }

    @Override
    // Cave Party menu command
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = ((Player) commandSender).getPlayer();

        if (PermissionManager.hasPermission(player, Permission.ALL_PARTY_COMMANDS)) {

            player.openInventory(new MainMenu().GUI(player));

        } else {
            commandSender.sendMessage(Utils.chat("&cYou can't do that right now :("));
        }

        return false;
    }
}
