package com.benschreiber;

import com.benschreiber.commands.CBMenu;
import com.benschreiber.commands.EndGame;
import com.benschreiber.commands.JoinParty;
import com.benschreiber.commands.LeaveParty;
import com.benschreiber.listeners.InventoryClickListener;
import com.benschreiber.listeners.UserEventListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    @Override
    public void onEnable() {
        new CBMenu(this);
        new JoinParty(this);
        new LeaveParty(this);
        new EndGame(this);
        new UserEventListener(this);
        new InventoryClickListener(this);
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }


}
