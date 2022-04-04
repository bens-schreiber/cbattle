package com.benschreiber.utils.itemstackbuilder;

import com.benschreiber.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

// Pretty way to build an item stack for GUI use.
public class ISBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    public ISBuilder(Material materialId) {
        this.item = new ItemStack(materialId);
        this.meta = item.getItemMeta();

    }

    public ISBuilder amount(int i) {
        item.setAmount(i);
        return this;
    }

    public ISBuilder addEnchants(EWrap... enchantments) {
        for (EWrap e : enchantments) {
            this.meta.addEnchant(e.enchantment, e.level, true);
        }
        return this;
    }

    public ISBuilder displayName(String displayName) {
        meta.setDisplayName(Utils.chat(displayName));
        return this;
    }

    public ISBuilder loreString(String... string) {
        List<String> loreString = new ArrayList<String>();
        for (String lore : string) {
            loreString.add(Utils.chat(lore));
        }
        meta.setLore(loreString);
        return this;
    }

    public ISBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ISBuilder playerHead(Player player) {

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return null;
        meta.setOwningPlayer(player);
        meta.setDisplayName(Utils.chat("&e" + player.getDisplayName()));
        this.meta = meta;
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

}
