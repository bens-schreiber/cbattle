package com.benschreiber.listeners;

import com.benschreiber.minigame.MiniGame;
import com.benschreiber.party.Party;
import com.benschreiber.party.PartyManager;
import com.benschreiber.Main;
import com.benschreiber.minigame.CaveBattle;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.Utils;
import com.benschreiber.utils.itemstackbuilder.ISBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class UserEventListener implements Listener {

    private static Plugin plugin;

    public UserEventListener(Main p) {
        Bukkit.getPluginManager().registerEvents(this, p);
        plugin = p;
    }

    /**
     * TNT place timer for Cave Battle
     */
    private static final HashMap<UUID, Long> TNT_TIMER_MAP = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void tntTimer(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (event.isCancelled()
                || !PartyManager.inAMiniGame(player)
                || event.getBlockPlaced().getType() != Material.TNT)
            return;


        if (TNT_TIMER_MAP.containsKey(player.getUniqueId())) {

            long remainingTime = TNT_TIMER_MAP.get(player.getUniqueId());

            if (remainingTime > Instant.now().getEpochSecond()) {
                event.setCancelled(true);
                player.sendMessage(Utils.chat("&4TNT TIMER: &bYou must wait " + (remainingTime - Instant.now().getEpochSecond()) + " seconds"));
                return;
            }

        }

        TNT_TIMER_MAP.put(player.getUniqueId(), Instant.now().getEpochSecond() + 8);

        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ISBuilder(Material.TNT)
                .displayName(Utils.chat("&4&lInfinite TNT"))
                .loreString(Utils.chat("&7TNT on an 8 second timer."))
                .amount(1)
                .build());


    }

    /**
     * CEgg place timer for Cave Battle
     */
    private static final HashMap<UUID, Long> CREEPER_TIMER_MAP = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creeperTimer(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!PartyManager.inAMiniGame(player)
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getItem() == null
                || event.getItem().getType() != Material.CREEPER_SPAWN_EGG) return;

        if (CREEPER_TIMER_MAP.containsKey(player.getUniqueId())) {

            long remainingTime = CREEPER_TIMER_MAP.get(player.getUniqueId());

            if (remainingTime > Instant.now().getEpochSecond()) {
                event.setCancelled(true);
                player.sendMessage(Utils.chat("&2Creeper Timer: &bYou must wait " + (remainingTime - Instant.now().getEpochSecond()) + " seconds"));
                return;
            }

        }

        CREEPER_TIMER_MAP.put(player.getUniqueId(), Instant.now().getEpochSecond() + 3);

        // PlayerInteractEvent is dumb, have to do this shit
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ISBuilder(Material.CREEPER_SPAWN_EGG)
                        .displayName(Utils.chat("&2&lInfinite Creeper"))
                        .loreString(Utils.chat("&7Creeper Eggs on a 3 second timer"))
                        .build());
            }
        }.runTaskLater(plugin, 1);
    }


    /**
     * Super Pickaxe item
     */
    @EventHandler
    public void superPickaxeBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!PartyManager.inAMiniGame(player)) return;

        if (player.isSneaking()) {

            if (player.getInventory().getItem(player.getInventory().getHeldItemSlot())
                    .getItemMeta().getDisplayName().equals(Utils.chat("&3&lSuper Pickaxe"))) {


                Location location = event.getBlock().getLocation();


                if (player.getLocation().getPitch() < -45 || player.getLocation().getPitch() > 45) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {

                            Block block = location.getWorld().getBlockAt(
                                    location.getBlockX() + x,
                                    location.getBlockY(),
                                    location.getBlockZ() + z);

                            if (!(block.getType() == Material.BEDROCK)) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }

                else if (player.getFacing() == BlockFace.NORTH || player.getFacing() == BlockFace.SOUTH) {

                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {

                            Block block = location.getWorld().getBlockAt(
                                    location.getBlockX() + x,
                                    location.getBlockY() + y,
                                    location.getBlockZ());

                            if (!(block.getType() == Material.BEDROCK)) {
                                block.setType(Material.AIR);
                            }
                        }
                    }

                } else {

                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Block block = location.getWorld().getBlockAt(
                                    location.getBlockX(),
                                    location.getBlockY() + y,
                                    location.getBlockZ() + z);

                            if (!(block.getType() == Material.BEDROCK)) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }

            }
        }
    }


    /**
     * Run commands.LeaveParty cmd on player disconnect
     */
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (!PartyManager.inAParty(player)) return;

        Party party = PartyManager.getParty(player);

        if (PartyManager.isAPartyLeader(player)) {

            if (party.inMiniGame()) party.getMiniGame().prematureEnd();

            if (party.getPlayers().size() == 0) PartyManager.disbandParty(party);
            else {
                party.setLeader(party.getPlayers().get(0));
                party.broadcast(party.getPlayers().get(0) + " is the new party leader.");
            }
        }

        PartyManager.removePartyMember(player);

        //Remove the players Cave Battle perms
        PermissionManager.removePlayer(player);
    }


    /**
     * Reset player on server join
     */
    // todo: check to see if player was in arena first use PDC
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        //Give players all party commands on join
        PermissionManager.addPlayer(player, Permission.ALL_PARTY_COMMANDS);

        player.getActivePotionEffects().clear();
        player.teleport(MiniGame.worldSpawnLocation);
        player.getActivePotionEffects().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100000, 100, true, false));
        player.getInventory().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCaveBattleDeath(PlayerDeathEvent event) {

        if (event.getEntity().getPlayer() == null) return;

        Player player = event.getEntity().getPlayer();

        if (!PartyManager.inAMiniGame(player)) return;

        if (player.getKiller() != null) {

            // Give killer 10 seconds of speed
            player.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
            player.getKiller().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat("&4&l>>> Player Killed! Speed Boost (10s) <<<")));

            // Death message
            PartyManager.getParty(player).broadcast(
                    player.getDisplayName() + " just got OWNED by " + player.getKiller().getDisplayName());

            PartyManager.getParty(player.getKiller()).getMiniGame().updateScoreBoard(player.getKiller());
        }

        // Drop only an apple on death
        event.getDrops().clear();
        event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

        // Spawn a firework on dead player
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta data = firework.getFireworkMeta();
        data.addEffect(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).build());
        data.setPower(1);
        firework.setFireworkMeta(data);
        firework.detonate();

        // Force player respawn
        new BukkitRunnable() {

            @Override
            public void run() {
                player.spigot().respawn();
            }
        }.runTaskLater(plugin, 10);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (PartyManager.inAMiniGame(player)) {

            event.setRespawnLocation(CaveBattle.randArenaLocation());

            new BukkitRunnable() {

                @Override
                public void run() {
                    PartyManager.getParty(player).getMiniGame().respawn(player);
                }
            }.runTaskLater(plugin, 1);
        } else {
            event.setRespawnLocation(MiniGame.worldSpawnLocation);
        }
    }

    /**
     * Set arrows shot from player in a mini game only do 1dmg
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowHit(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)
                || !(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))
                || !(event.getDamager().getType().equals(EntityType.ARROW)))
            return;

        Player player = (Player) event.getEntity();
        if (!PartyManager.inAMiniGame(player)) return;

        event.setDamage(1);
    }

    @EventHandler
    public void effectBlocks(BlockBreakEvent event) {

        Player player = event.getPlayer();
        if (!PartyManager.inAMiniGame(player)) return;


        switch (event.getBlock().getType()) {
            case DIAMOND_BLOCK -> {
                event.getBlock().setType(Material.AIR);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat("&b&l>>> Increased Strength (15s) <<<")));
            }
            case IRON_BLOCK -> {
                event.getBlock().setType(Material.AIR);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat("&f&l>>> Damage Resistance (15s) <<<")));
            }
            case EMERALD_BLOCK -> {
                event.getBlock().setType(Material.AIR);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat("&a&l>>> Regenerating Health (10s) <<<")));
            }
            case NETHERRACK -> event.getBlock().setType(Material.AIR);
        }
    }
}