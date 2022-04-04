package com.benschreiber.minigame;

import com.benschreiber.Main;
import com.benschreiber.party.Party;
import com.benschreiber.perms.Permission;
import com.benschreiber.perms.PermissionManager;
import com.benschreiber.utils.Utils;
import com.benschreiber.utils.itemstackbuilder.EWrap;
import com.benschreiber.utils.itemstackbuilder.ISBuilder;
import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CaveBattle extends MiniGame {

    public CaveBattle(Party party) {
        this.party = party;
        this.maxKills = party.getLeader();
    }

    private final Set<BukkitTask> delayedTasks = new HashSet<>();

    private final JGlobalMethodBasedScoreboard scoreboard = new JGlobalMethodBasedScoreboard();

    private final HashMap<Player, Integer> kills = new HashMap<>();
    private Player maxKills;

    private void updateKills(Player player, int n) {
        kills.put(player, n);
        if (kills.get(player) > kills.get(maxKills)) maxKills = player;
    }


    public void start() {

        party.setMiniGame(this);
        party.broadcast("&c&lResetting arena...");
        resetArena();

        for (Player player : party.getPlayers()) {

            updateKills(player, 0);

            // Remove the ability to do party commands while in the game
            PermissionManager.removePerm(player, Permission.ALL_PARTY_COMMANDS);

            // Initialize the player
            initializePlayer(player);

            // Send splash text
            player.sendTitle(Utils.formatMsg("Begin the battle!"), null, 1, 100, 1);

            scoreboard.addPlayer(player);

        }

        initScoreBoard();

        // Initialize delayed splash texts
        initSplashTexts();

        // End the game at 7200 ticks (6 minutes)
        delayedTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                end();
            }
        }.runTaskLater(Main.getInstance(), 7200));

    }

    private void end() {
        for (Player player : party.getPlayers()) {

            // Send splash text
            player.sendTitle(Utils.formatMsg(maxKills.getDisplayName() + " is the winner!"), null, 1, 60, 1);

            // Reset players positions and inventory
            player.getInventory().clear();
            player.teleport(worldSpawnLocation);
            player.getActivePotionEffects().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100000, 100, true, true));
            player.setHealth(20);

            // Let player do party commands
            PermissionManager.addPerm(player, Permission.ALL_PARTY_COMMANDS);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wither]");
        }

        party.setMiniGame(null);
        scoreboard.destroy();
        kills.clear();
        this.party = null;
    }

    public void prematureEnd() {
        delayedTasks.forEach(BukkitTask::cancel);
        party.broadcast("&cGame ended early :((((");
        end();
    }

    public void respawn(Player player) {
        initializePlayer(player);
    }

    private static void initializePlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);

        ItemStack[] arr = new ItemStack[4];
        arr[0] = new ItemStack(Material.DIAMOND_BOOTS);
        arr[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        arr[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        arr[3] = new ItemStack(Material.DIAMOND_HELMET);
        player.getInventory().setArmorContents(arr);

        player.getInventory().setItemInMainHand(new ISBuilder(Material.DIAMOND_PICKAXE)
                .addEnchants(
                        new EWrap(Enchantment.DIG_SPEED, 5),
                        new EWrap(Enchantment.KNOCKBACK, 2)
                )
                .unbreakable(true)
                .displayName(Utils.chat("&3&lSuper Pickaxe"))
                .loreString(Utils.chat("&7Mine a 3x1x1 chunk if sneaking!"))
                .build()
        );

        player.getInventory().setItemInOffHand(new ISBuilder(Material.SHIELD)
                .displayName(Utils.chat("&e&lShield"))
                .loreString(Utils.chat("&7No you"))
                .unbreakable(true)
                .build());

        player.getInventory().addItem(new ISBuilder(Material.BOW)
                .displayName(Utils.chat("&b&lLighter"))
                .loreString(Utils.chat("&7Lights any TNT or person on fire!"))
                .addEnchants(
                        new EWrap(Enchantment.ARROW_FIRE, 1),
                        new EWrap(Enchantment.ARROW_INFINITE, 1)
                )
                .unbreakable(true)
                .build());

        player.getInventory().addItem(new ISBuilder(Material.TNT)
                .displayName(Utils.chat("&4&lInfinite TNT"))
                .loreString(Utils.chat("&7TNT on an 8 second timer."))
                .build());

        player.getInventory().addItem(new ISBuilder(Material.CREEPER_SPAWN_EGG)
                .displayName(Utils.chat("&2&lInfinite Creeper"))
                .loreString(Utils.chat("&7Creeper Eggs on a 3 second timer"))
                .build());


        player.getInventory().addItem(new ISBuilder(Material.GOLDEN_APPLE)
                .build());

        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000, 2));


        // Teleport to a random location within the arena
        player.teleport(randArenaLocation());

    }

    private void resetArena() {

        World world = Bukkit.getWorld("minigame");
        Location edgeMin = new Location(world, -64, 4, -63);
        Location edgeMax = new Location(world, -1, 64, 0);

        for (int x = edgeMin.getBlockX(); x <= edgeMax.getBlockX(); x++) {
            for (int y = edgeMin.getBlockY(); y <= edgeMax.getBlockY(); y++) {
                for (int z = edgeMin.getBlockZ(); z <= edgeMax.getBlockZ(); z++) {

                    Block block = new Location(world, x, y, z).getBlock();
                    if (block.getType() == Material.AIR
                            || block.getType() == Material.FIRE
                            || block.getType() == Material.TNT) {

                        int rand = new Random().nextInt(150);

                        if (rand == 1) {
                            block.setType(Material.DIAMOND_BLOCK);
                        } else if (rand == 2) {
                            block.setType(Material.EMERALD_BLOCK);
                        } else if (rand == 3) {
                            block.setType(Material.IRON_BLOCK);
                        } else if (rand < 34) {
                            block.setType(Material.COBBLESTONE);
                        } else if (rand < 70) {
                            block.setType(Material.NETHERRACK);
                        } else {
                            block.setType(Material.STONE);
                        }
                    }
                }

            }

        }


        party.broadcast("Done!");
    }

    public static Location randArenaLocation() {
        int x = ThreadLocalRandom.current().nextInt(-61, 0);
        int y = ThreadLocalRandom.current().nextInt(4, 60);
        int z = ThreadLocalRandom.current().nextInt(-61, 0);

        Location location = new Location(Bukkit.getWorld("minigame"), x, y, z);
        prepareLocation(location);

        return location;
    }

    private static void prepareLocation(Location location) {
        double y = location.getY();
        for (int i = 0; i < 2; i++) {
            location.setY(y + i);
            location.getBlock().setType(Material.AIR);
        }
        location.setY(y);
        location.setX(location.getX() + .5);
        location.setZ(location.getZ() + .5);
    }

    private void initSplashTexts() {

        delayedTasks.addAll(Arrays.asList(new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : party.getPlayers()) {
                    player.sendTitle(Utils.formatMsg("3 minutes remain"), null, 1, 20, 1);
                }
            }
        }.runTaskLater(Main.getInstance(), 3600), new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : party.getPlayers()) {
                    player.sendTitle(Utils.formatMsg("A wither is spawning!! "), "Holy Cow!", 1, 20, 1);
                }

                Location location = randArenaLocation();
                prepareLocation(location);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "summon minecraft:wither " + location.getX() + " " + location.getY() + " " + location.getZ()
                                + " {ActiveEffects:[{Id:24,Duration:10000,Amplifier:0,ShowParticles:0b}]} ");
            }
        }.runTaskLater(Main.getInstance(), 4800), new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : party.getPlayers()) {
                    player.sendTitle(Utils.formatMsg("1 minute remains!"), null, 1, 20, 1);
                }
            }
        }.runTaskLater(Main.getInstance(), 6000)));
    }

    private void initScoreBoard() {
        scoreboard.setTitle("&c&lKill Board");

        scoreboard.setLines(party
                .getPlayers()
                .stream()
                .map(p -> "&a" + p.getDisplayName() + " &4&l" + 0)
                .collect(Collectors.toList())
        );

        scoreboard.updateScoreboard();
    }

    public void updateScoreBoard(Player player) {

        updateKills(player, kills.get(player) + 1);

        scoreboard.setLines(party
                .getPlayers()
                .stream()
                .map(p -> "&a" + p.getDisplayName() + " &4&l" + kills.get(p))
                .collect(Collectors.toList())
        );

    }
}