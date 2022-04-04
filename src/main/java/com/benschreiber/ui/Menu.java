package com.benschreiber.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Menu {

    protected Inventory inventory;

    public abstract Inventory GUI(Player player);
    public abstract void clicked(Player player, int slot, ItemStack clicked, Inventory inventory);
}
