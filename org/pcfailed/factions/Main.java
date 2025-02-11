package org.pcfailed.factions;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.OfflinePlayer;
import org.pcfailed.factions.extras.Pair;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Fireball;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    Thread th;
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)new KillListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InventoryClickListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InvCloseListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DamageListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DamageListener2(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new MoveListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerInteract(), (Plugin)this);
        this.getConfig().set("INIT", (Object)true);
        this.saveConfig();
        this.getCommand("faction").setExecutor((CommandExecutor)new FacExecutor());
        this.getCommand("fadmin").setExecutor((CommandExecutor)new FacAdminExecutor());
        this.getCommand("facyes").setExecutor((CommandExecutor)new AcceptListener());
        this.getCommand("facno").setExecutor((CommandExecutor)new DenyListener());
        (this.th = new t()).start();
    }
    
    public void onDisable() {
        this.saveConfig();
        this.th.interrupt();
    }
    
    public class t extends Thread
    {
        @Override
        public void run() {
            final Collection<? extends Player> players = (Collection<? extends Player>)Main.this.getServer().getOnlinePlayers();
            while (true) {
                for (final Player player : players) {
                    if (Main.this.getConfig().contains(player.getUniqueId().toString())) {
                        final String test = Main.this.getConfig().getString(Main.this.getConfig().getString(player.getUniqueId().toString()));
                        if (test != null || Main.this.getConfig().contains(String.valueOf(player.getUniqueId().toString()) + "@CREATING")) {
                            continue;
                        }
                        Main.this.getConfig().set(player.getUniqueId().toString(), (Object)null);
                        Main.this.getConfig().set(String.valueOf(player.getUniqueId().toString()) + "=PowerGenerated", (Object)null);
                        Main.this.saveConfig();
                        Main.this.getLogger().info(String.valueOf(player.getName()) + " had a null faction, correcting..");
                    }
                }
            }
        }
    }
    
    public class MoveListener implements Listener
    {
        @EventHandler
        public void onPlayerMove(final PlayerMoveEvent e) {
            try {
                if (e.getPlayer().getInventory().getHelmet().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:SPEEDHELMET")) {
                    if (e.getPlayer().hasPermission("pcfailed.factions.ability.speedhelmet")) {
                        final PotionEffectType pet = PotionEffectType.SPEED;
                        final PotionEffect pe = new PotionEffect(pet, 1000000, 1);
                        e.getPlayer().addPotionEffect(pe);
                        Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}SPEED", (Object)true);
                        Main.this.saveConfig();
                    }
                }
                else if (Main.this.getConfig().contains(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}SPEED")) {
                    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}SPEED", (Object)null);
                    Main.this.saveConfig();
                }
                if (e.getPlayer().getInventory().getHelmet().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:MULTIHELMET")) {
                    if (e.getPlayer().hasPermission("pcfailed.factions.ability.multihelmet")) {
                        final PotionEffectType pet = PotionEffectType.DAMAGE_RESISTANCE;
                        final PotionEffect pe = new PotionEffect(pet, 1000000, 0);
                        final PotionEffectType pet2 = PotionEffectType.SLOW;
                        final PotionEffect pe2 = new PotionEffect(pet2, 1000000, 0);
                        e.getPlayer().addPotionEffect(pe);
                        e.getPlayer().addPotionEffect(pe2);
                        Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}MULTI", (Object)true);
                        Main.this.saveConfig();
                    }
                }
                else if (Main.this.getConfig().contains(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}MULTI")) {
                    e.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                    Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}MULTI", (Object)null);
                    Main.this.saveConfig();
                }
            }
            catch (Exception exc) {
                if (e.getPlayer().hasPermission("pcfailed.factions.ability.speedhelmet") && Main.this.getConfig().contains(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}SPEED")) {
                    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}SPEED", (Object)null);
                    Main.this.saveConfig();
                }
                if (e.getPlayer().hasPermission("pcfailed.factions.ability.speedhelmet") && Main.this.getConfig().contains(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}MULTI")) {
                    e.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                    Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + "}MULTI", (Object)null);
                    Main.this.saveConfig();
                }
            }
        }
    }
    
    public class PlayerInteract implements Listener
    {
        @EventHandler
        public void onPlayerRC(final PlayerInteractEvent ev) {
            try {
                if (ev.getAction().equals((Object)Action.RIGHT_CLICK_AIR) || ev.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
                    if (ev.getPlayer().hasPermission("pcfailed.factions.ability.aote")) {
                        if (ev.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:AOTE")) {
                            if (Main.this.getConfig().getBoolean(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[AOTE")) {
                                return;
                            }
                            final Vector v = ev.getPlayer().getLocation().getDirection();
                            v.multiply(4);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Factions_Ultimate"), (Runnable)new Runnable() {
                                @Override
                                public void run() {
                                    Main.this.getConfig().set(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[AOTE", (Object)null);
                                }
                            }, 5000L);
                            final Location l = ev.getPlayer().getLocation().add(v);
                            if (l.getBlock().getType().equals((Object)Material.AIR)) {
                                ev.getPlayer().teleport(l);
                                Main.this.getConfig().set(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[AOTE", (Object)true);
                                Main.this.saveConfig();
                            }
                        }
                        if (ev.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:AOTENC")) {
                            final Vector v = ev.getPlayer().getLocation().getDirection();
                            v.multiply(4);
                            final Location l = ev.getPlayer().getLocation().add(v);
                            ev.getPlayer().teleport(l);
                        }
                    }
                    if (ev.getPlayer().hasPermission("pcfailed.factions.ability.fbsword") && ev.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:FBSWORD")) {
                        if (Main.this.getConfig().getBoolean(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[FBSWORD")) {
                            return;
                        }
                        final Fireball f = (Fireball)ev.getPlayer().launchProjectile((Class)Fireball.class);
                        f.setIsIncendiary(true);
                        Main.this.getConfig().set(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[FBSWORD", (Object)true);
                        Main.this.saveConfig();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Factions_Ultimate"), (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                Main.this.getConfig().set(String.valueOf(ev.getPlayer().getUniqueId().toString()) + "[AOTE", (Object)null);
                            }
                        }, 5000L);
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public class KillListener implements Listener
    {
        @EventHandler
        public void onPlayerDeath(final EntityDeathEvent e) {
            try {
                if (!e.getEntity().getKiller().getType().equals((Object)EntityType.PLAYER)) {
                    final Entity dead = (Entity)e.getEntity();
                    if (dead.getType().equals((Object)EntityType.PLAYER)) {
                        final Faction f2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(dead.getUniqueId().toString())));
                        f2.addPower(-1.0);
                        dead.sendMessage(ChatColor.RED + "-1 Power - entity.kill.player.event");
                        Main.this.getConfig().set(f2.name, (Object)f2.serialize());
                        Main.this.saveConfig();
                        return;
                    }
                }
                if (!e.getEntityType().equals((Object)EntityType.PLAYER)) {
                    final Player killer = e.getEntity().getKiller();
                    if (e.getEntity().getType().equals((Object)EntityType.ENDERMAN)) {
                        return;
                    }
                    final Faction f3 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(killer.getUniqueId().toString())));
                    f3.addPower(0.2);
                    killer.sendMessage(ChatColor.DARK_GREEN + "+0.2 Power");
                    if (Main.this.getConfig().get(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated") == null) {
                        Main.this.getConfig().set(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated", (Object)0.2);
                    }
                    else {
                        Main.this.getConfig().set(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated", (Object)(Main.this.getConfig().getInt(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated") + 0.2));
                    }
                    Main.this.saveConfig();
                    Main.this.getConfig().set(f3.name, (Object)f3.serialize());
                }
                else {
                    final Player killer = e.getEntity().getKiller();
                    final Player dead2 = (Player)e.getEntity();
                    final Faction f4 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(killer.getUniqueId().toString())));
                    final Faction f5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(dead2.getUniqueId().toString())));
                    if (Main.this.getConfig().get(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated") == null) {
                        Main.this.getConfig().set(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated", (Object)3);
                    }
                    else {
                        Main.this.getConfig().set(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated", (Object)(Main.this.getConfig().getInt(String.valueOf(killer.getUniqueId().toString()) + "=PowerGenerated") + 3));
                    }
                    if (killer.equals(dead2)) {
                        killer.sendMessage(ChatColor.DARK_GREEN + "-1 Power");
                        f4.addPower(-1.0);
                        return;
                    }
                    f4.addPower(3.0);
                    killer.sendMessage(ChatColor.YELLOW + "+3 Power");
                    f5.addPower(-4.0);
                    dead2.sendMessage(ChatColor.RED + "-4 Power");
                    Main.this.getConfig().set(Main.this.getConfig().getString(killer.getUniqueId().toString()), (Object)f4.serialize());
                    Main.this.getConfig().set(Main.this.getConfig().getString(dead2.getUniqueId().toString()), (Object)f5.serialize());
                    Main.this.saveConfig();
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public class InventoryClickListener implements Listener
    {
        @EventHandler
        public void onPlayerClickInv(final InventoryClickEvent e) {
            if (Main.this.getConfig().getBoolean(String.valueOf(e.getWhoClicked().getUniqueId().toString()) + ";perkmenu")) {
                final Faction fac = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(e.getWhoClicked().getUniqueId().toString())));
                final OfflinePlayer[] o = Bukkit.getOfflinePlayers();
                if (e.getSlot() == 1) {
                    if (fac.power > 4.0) {
                        fac.removePower(5.0);
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x5 from faction....");
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You teleported all Faction members to you for 5 FP.");
                        OfflinePlayer[] array;
                        for (int length = (array = o).length, j = 0; j < length; ++j) {
                            final OfflinePlayer opr = array[j];
                            if (fac.name.equals(Main.this.getConfig().getString(opr.getUniqueId().toString()))) {
                                final Player p = opr.getPlayer();
                                p.teleport((Entity)e.getWhoClicked());
                            }
                        }
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                    Main.this.getConfig().set(fac.name, (Object)fac.serialize());
                }
                if (e.getSlot() == 7) {
                    if (fac.power > 9.0) {
                        fac.removePower(10.0);
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x10 from faction....");
                        final PotionEffect f = new PotionEffect(PotionEffectType.SATURATION, 100, 0);
                        OfflinePlayer[] array2;
                        for (int length2 = (array2 = o).length, k = 0; k < length2; ++k) {
                            final OfflinePlayer opr2 = array2[k];
                            if (fac.name.equals(Main.this.getConfig().getString(opr2.getUniqueId().toString()))) {
                                final Player p2 = opr2.getPlayer();
                                e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought Saturation for 10 FP.");
                                if (p2.isOnline()) {
                                    p2.addPotionEffect(f);
                                }
                            }
                        }
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 9) {
                    if (fac.power > 24.0) {
                        fac.removePower(25.0);
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x25 from faction....");
                        final PotionEffect f = new PotionEffect(PotionEffectType.SPEED, 1000, 1);
                        OfflinePlayer[] array3;
                        for (int length3 = (array3 = o).length, l = 0; l < length3; ++l) {
                            final OfflinePlayer opr2 = array3[l];
                            if (fac.name.equals(Main.this.getConfig().getString(opr2.getUniqueId().toString()))) {
                                final Player p2 = opr2.getPlayer();
                                e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought Speed II for 1000 ticks for 25 FP.");
                                if (p2.isOnline()) {
                                    p2.addPotionEffect(f);
                                }
                            }
                        }
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 11) {
                    if (fac.power > 49.0) {
                        fac.removePower(50.0);
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x50 from faction....");
                        final PotionEffect f = new PotionEffect(PotionEffectType.REGENERATION, 1000, 3);
                        OfflinePlayer[] array4;
                        for (int length4 = (array4 = o).length, n = 0; n < length4; ++n) {
                            final OfflinePlayer opr2 = array4[n];
                            if (fac.name.equals(Main.this.getConfig().getString(opr2.getUniqueId().toString()))) {
                                final Player p2 = opr2.getPlayer();
                                e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought Regeneration IV for 1000 ticks for 50 FP.");
                                if (p2.isOnline()) {
                                    p2.addPotionEffect(f);
                                }
                            }
                        }
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 13) {
                    if (fac.power > 99.0) {
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x100 from faction....");
                        fac.removePower(100.0);
                        final PlayerInventory p3 = e.getWhoClicked().getInventory();
                        p3.addItem(new ItemStack[] { new ItemStack(Material.DIAMOND, 30) });
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought 30 Diamonds for 100FP.");
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 15) {
                    if (fac.power > 149.0) {
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x150 from faction....");
                        fac.removePower(150.0);
                        final ItemStack i = new ItemStack(Material.NETHERITE_CHESTPLATE);
                        final ItemStack i2 = new ItemStack(Material.NETHERITE_CHESTPLATE);
                        final ItemStack i3 = new ItemStack(Material.NETHERITE_CHESTPLATE);
                        final ItemMeta im = i.getItemMeta();
                        final ItemMeta im2 = i2.getItemMeta();
                        final ItemMeta im3 = i3.getItemMeta();
                        final List<String> lore = new ArrayList<String>();
                        final List<String> lore2 = new ArrayList<String>();
                        final List<String> lore3 = new ArrayList<String>();
                        lore.add(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC1");
                        lore2.add(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC2");
                        lore3.add(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC3");
                        im.setLore((List)lore);
                        im2.setLore((List)lore2);
                        im3.setLore((List)lore3);
                        im.setDisplayName("Superior Netherite Chestplate");
                        im2.setDisplayName("Hardened Netherite Chestplate");
                        im3.setDisplayName("Strong Netherite Chestplate");
                        im.addEnchant(Enchantment.DURABILITY, 3, true);
                        im2.addEnchant(Enchantment.DURABILITY, 3, true);
                        im3.addEnchant(Enchantment.DURABILITY, 3, true);
                        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                        im2.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                        im3.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                        i.setItemMeta(im);
                        i2.setItemMeta(im2);
                        i3.setItemMeta(im3);
                        final List<Pair<OfflinePlayer, Integer>> doubles = new ArrayList<Pair<OfflinePlayer, Integer>>();
                        OfflinePlayer[] array5;
                        for (int length5 = (array5 = o).length, n2 = 0; n2 < length5; ++n2) {
                            final OfflinePlayer opr3 = array5[n2];
                            if (Main.this.getConfig().getString(e.getWhoClicked().getUniqueId().toString()).equals(Main.this.getConfig().getString(opr3.getUniqueId().toString()))) {
                                final Integer integer = Main.this.getConfig().getInt(String.valueOf(opr3.getPlayer().getUniqueId().toString()) + "=PowerGenerated");
                                final Pair<OfflinePlayer, Integer> pair = new Pair<OfflinePlayer, Integer>(opr3, integer);
                                doubles.add(pair);
                            }
                        }
                        int max = 0;
                        final List<Pair<OfflinePlayer, Integer>> ordered = new ArrayList<Pair<OfflinePlayer, Integer>>();
                        for (final Pair<OfflinePlayer, Integer> pairs : doubles) {
                            if (Integer.max(pairs.getB(), max) == pairs.getB()) {
                                max = pairs.getB();
                                ordered.add(pairs);
                            }
                        }
                        try {
                            if (ordered.get(ordered.size() - 1).getA().isOnline()) {
                                final Player p4 = ordered.get(ordered.size() - 1).getA().getPlayer();
                                p4.sendMessage(ChatColor.GREEN + "You won the Superior Netherite Chestplate!");
                                ordered.get(ordered.size() - 1).getA().getPlayer().getInventory().addItem(new ItemStack[] { i });
                            }
                            else {
                                ordered.get(ordered.size() - 1).getA().getPlayer().getInventory().addItem(new ItemStack[] { i });
                            }
                            if (ordered.get(ordered.size() - 2).getA().isOnline()) {
                                final Player p4 = ordered.get(ordered.size() - 2).getA().getPlayer();
                                p4.sendMessage(ChatColor.GREEN + "You won the Hardened Netherite Chestplate!");
                                ordered.get(ordered.size() - 2).getA().getPlayer().getInventory().addItem(new ItemStack[] { i2 });
                            }
                            else {
                                ordered.get(ordered.size() - 2).getA().getPlayer().getInventory().addItem(new ItemStack[] { i2 });
                            }
                            if (ordered.get(ordered.size() - 2).getA().isOnline()) {
                                final Player p4 = ordered.get(ordered.size() - 3).getA().getPlayer();
                                ordered.get(ordered.size() - 3).getA().getPlayer().getInventory().addItem(new ItemStack[] { i3 });
                                p4.sendMessage(ChatColor.GREEN + "You won the Strong Netherite Chestplate!");
                            }
                            else {
                                ordered.get(ordered.size() - 3).getA().getPlayer().getInventory().addItem(new ItemStack[] { i3 });
                            }
                        }
                        catch (Exception ec) {
                            e.setCancelled(true);
                        }
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                    e.setCancelled(true);
                }
                if (e.getSlot() == 3) {
                    if (fac.power > 7.0) {
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x6 from faction....");
                        fac.removePower(6.0);
                        final PlayerInventory p3 = e.getWhoClicked().getInventory();
                        p3.addItem(new ItemStack[] { new ItemStack(Material.SADDLE) });
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought a Saddle for 6FP.");
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 5) {
                    if (fac.power > 10.0) {
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x9 from faction....");
                        fac.removePower(6.0);
                        final PlayerInventory p3 = e.getWhoClicked().getInventory();
                        p3.addItem(new ItemStack[] { new ItemStack(Material.EXPERIENCE_BOTTLE, 32) });
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought 32 Experience Bottles for 9FP.");
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                if (e.getSlot() == 17) {
                    if (fac.power > 19.0) {
                        e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Taking faction power x20 from faction....");
                        fac.removePower(20.0);
                        ((Player)e.getWhoClicked()).setAllowFlight(true);
                        ((Player)e.getWhoClicked()).setGravity(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Factions_Ultimate"), (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((Player)e.getWhoClicked()).setAllowFlight(false);
                                    ((Player)e.getWhoClicked()).sendMessage(ChatColor.GREEN + "Your flight has run out!");
                                }
                                catch (Exception ex) {}
                            }
                        }, 12000L);
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You bought flight mode. Poggers.");
                    }
                    else {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough faction power.");
                    }
                }
                Main.this.getConfig().set(fac.name, (Object)fac.serialize());
                Main.this.saveConfig();
                e.setCancelled(true);
            }
        }
    }
    
    public class InvCloseListener implements Listener
    {
        @EventHandler
        public void onInvClose(final InventoryCloseEvent e) {
            Main.this.getConfig().set(String.valueOf(e.getPlayer().getUniqueId().toString()) + ";perkmenu", (Object)null);
            Main.this.saveConfig();
        }
    }
    
    public class DamageListener implements Listener
    {
        @EventHandler
        public void onDamage(final EntityDamageEvent e) {
            if (e.getEntityType().equals((Object)EntityType.PLAYER)) {
                final Player p = (Player)e.getEntity();
                if (p.getInventory().getChestplate() == null) {
                    return;
                }
                final ItemStack i = p.getInventory().getChestplate();
                final ItemMeta im = i.getItemMeta();
                if (im.hasLore()) {
                    final List<String> lore = (List<String>)im.getLore();
                    if (lore.contains(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC1")) {
                        e.setDamage(e.getDamage() * 0.02);
                    }
                    else if (lore.contains(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC2")) {
                        e.setDamage(e.getDamage() * 0.04);
                    }
                    else if (lore.contains(ChatColor.DARK_GRAY + "CUSTOM_ID:SNC3")) {
                        e.setDamage(e.getDamage() * 0.06);
                    }
                }
            }
        }
        
        @EventHandler
        public void onDamage(final EntityDamageByEntityEvent e) {
            final Entity damager = e.getDamager();
            final Entity damaged = e.getEntity();
            if (damager.getType().equals((Object)EntityType.PLAYER)) {
                try {
                    final Player p = (Player)damager;
                    if (p.isFlying()) {
                        e.setCancelled(true);
                        p.sendMessage(ChatColor.DARK_GRAY + "You cannot attack whilst flying.");
                        if (p.isOp()) {
                            p.sendMessage(ChatColor.RED + "Admin, you thought you could cheat..... tut tut tut.");
                        }
                    }
                    if (((Player)damager).getInventory().getItemInMainHand().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:EFFECTSTICK")) {
                        if (((Player)damager).hasPermission("pcfailed.factions.ability.effectstick")) {
                            final String f1 = Main.this.getConfig().getString(e.getDamager().getUniqueId().toString());
                            final String f2 = Main.this.getConfig().getString(e.getEntity().getUniqueId().toString());
                            if (f1 != null && f2 != null && e.getDamager().getType().equals((Object)EntityType.PLAYER) && f1.equals(f2)) {
                                e.setCancelled(true);
                                return;
                            }
                            final PotionEffect pe1 = new PotionEffect(PotionEffectType.HUNGER, 100, 0);
                            final PotionEffect pe2 = new PotionEffect(PotionEffectType.CONFUSION, 100, 99);
                            final PotionEffect pe3 = new PotionEffect(PotionEffectType.WITHER, 5, 0);
                            Main.this.getLogger().info("Entity damaged with stick, cannot add effect.");
                            final Player pd = (Player)damaged;
                            pd.addPotionEffect(pe1);
                            pd.addPotionEffect(pe2);
                            pd.addPotionEffect(pe3);
                        }
                        if (((Player)damager).hasPermission("pcfailed.factions.ability.effectstick2")) {
                            final String f1 = Main.this.getConfig().getString(e.getDamager().getUniqueId().toString());
                            final String f2 = Main.this.getConfig().getString(e.getEntity().getUniqueId().toString());
                            if (f1 != null && f2 != null && e.getDamager().getType().equals((Object)EntityType.PLAYER) && f1.equals(f2)) {
                                e.setCancelled(true);
                                return;
                            }
                            final PotionEffect pe1 = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0);
                            final PotionEffect pe2 = new PotionEffect(PotionEffectType.CONFUSION, 100, 99);
                            final PotionEffect pe3 = new PotionEffect(PotionEffectType.HUNGER, 10, 100);
                            final Player pd = (Player)damaged;
                            pd.addPotionEffect(pe1);
                            pd.addPotionEffect(pe2);
                            pd.addPotionEffect(pe3);
                        }
                    }
                }
                catch (Exception ex) {}
                try {
                    if (((Player)damager).getInventory().getItemInOffHand().getItemMeta().getLore().contains(ChatColor.DARK_GRAY + "CUSTOM_ID:EFFECTSTICK") && ((Player)damager).hasPermission("pcfailed.factions.ability.effectstick")) {
                        final String f3 = Main.this.getConfig().getString(e.getDamager().getUniqueId().toString());
                        final String f4 = Main.this.getConfig().getString(e.getEntity().getUniqueId().toString());
                        if (f3 != null && f4 != null && e.getDamager().getType().equals((Object)EntityType.PLAYER) && f3.equals(f4)) {
                            e.setCancelled(true);
                            return;
                        }
                        final PotionEffect pe4 = new PotionEffect(PotionEffectType.HUNGER, 100, 0);
                        final PotionEffect pe5 = new PotionEffect(PotionEffectType.CONFUSION, 100, 99);
                        final PotionEffect pe6 = new PotionEffect(PotionEffectType.WITHER, 5, 0);
                        Main.this.getLogger().info("Entity damaged with stick, cannot add effect.");
                        final Player p2 = (Player)damaged;
                        p2.addPotionEffect(pe4);
                        p2.addPotionEffect(pe5);
                        p2.addPotionEffect(pe6);
                    }
                }
                catch (Exception ex2) {}
            }
        }
    }
    
    public class DamageListener2 implements Listener
    {
        @EventHandler
        public void onDamage2(final EntityDamageByEntityEvent ev) {
            try {
                final String f1 = Main.this.getConfig().getString(ev.getDamager().getUniqueId().toString());
                final String f2 = Main.this.getConfig().getString(ev.getEntity().getUniqueId().toString());
                if (f1.equals(f2) && f1 != null && f2 != null && ev.getDamager().getType().equals((Object)EntityType.PLAYER)) {
                    ev.setCancelled(true);
                    final Player p = (Player)ev.getDamager();
                    p.sendMessage(ChatColor.DARK_GRAY + "You cannot damage others on your faction.");
                }
                final Faction fc1 = Faction.deserialize(Main.this.getConfig().getString(f1));
                final Faction fc2 = Faction.deserialize(Main.this.getConfig().getString(f2));
                if (fc1.allies.contains(fc2.name)) {
                    ev.setCancelled(true);
                    final Player p2 = (Player)ev.getDamager();
                    p2.sendMessage(ChatColor.DARK_GRAY + "You cannot damage your allies.");
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public class FacExecutor implements CommandExecutor
    {
        public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
            try {
                final Player p = (Player)sender;
                if (args[0].toLowerCase().toLowerCase().toLowerCase().equals("create")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "You have not specified a name for your faction.");
                        return false;
                    }
                    if (Main.this.getConfig().getString(p.getUniqueId().toString()) == null) {
                        Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + "@CREATING", (Object)true);
                        Main.this.saveConfig();
                        final List<String> allies = new ArrayList<String>();
                        allies.add("Q");
                        final List<String> wars = new ArrayList<String>();
                        wars.add("Q");
                        final Faction fac = new Faction(String.valueOf(args[1]) + "=" + p.getUniqueId().toString(), p.getUniqueId(), 0.0, allies, wars);
                        Main.this.getConfig().set(p.getUniqueId().toString(), (Object)(String.valueOf(args[1]) + "=" + p.getUniqueId().toString()));
                        Main.this.getConfig().set(String.valueOf(args[1]) + "=" + p.getUniqueId().toString(), (Object)fac.serialize());
                        Main.this.getConfig().set(p.getUniqueId().toString(), (Object)fac.name);
                        Main.this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Your new faction " + args[1] + " has been created.");
                        Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + "@CREATING", (Object)null);
                        Main.this.saveConfig();
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "You are already in a faction.");
                    }
                }
                else if (args[0].toLowerCase().toLowerCase().toLowerCase().equals("invite")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "You have not specified a name to invite.");
                        return false;
                    }
                    if (!Main.this.getConfig().contains(p.getUniqueId().toString())) {
                        sender.sendMessage(ChatColor.RED + "You are not in a faction.");
                        return false;
                    }
                    if (p == Bukkit.getPlayer(args[1])) {
                        sender.sendMessage(ChatColor.RED + "You cannot invite yourself!");
                        return true;
                    }
                    final Player pl = Bukkit.getPlayer(args[1]);
                    if (pl == null) {
                        sender.sendMessage(ChatColor.RED + "This player is not online.");
                    }
                    final String fname = Main.this.getConfig().getString(p.getUniqueId().toString());
                    sender.sendMessage(ChatColor.GREEN + "You invited " + args[1] + " to a faction.");
                    pl.sendMessage(ChatColor.YELLOW + "You have been invited to faction " + fname.split("=")[0] + " by " + p.getName() + ".\nDo /facyes " + p.getName() + " to accept.");
                    if (Main.this.getConfig().getString(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString()) != null) {
                        sender.sendMessage(ChatColor.RED + "You have already invited this person to a faction.");
                        return false;
                    }
                    Main.this.getConfig().set(String.valueOf(pl.getUniqueId().toString()) + "=INV=" + p.getUniqueId().toString(), (Object)fname);
                    Main.this.saveConfig();
                }
                else if (args[0].toLowerCase().toLowerCase().equals("perk")) {
                    Faction fac2;
                    try {
                        fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    }
                    catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + "You are not in a faction.");
                        return false;
                    }
                    if (p.getUniqueId().equals(fac2.leader)) {
                        final ItemStack i1 = new ItemStack(Material.COMMAND_BLOCK);
                        final ItemStack i2 = new ItemStack(Material.COOKED_BEEF);
                        final ItemStack i3 = new ItemStack(Material.NETHERITE_BOOTS);
                        final ItemStack i4 = new ItemStack(Material.REDSTONE_BLOCK);
                        final ItemStack i5 = new ItemStack(Material.DIAMOND);
                        final ItemStack i6 = new ItemStack(Material.NETHERITE_CHESTPLATE);
                        final ItemStack i7 = new ItemStack(Material.SADDLE);
                        final ItemStack i8 = new ItemStack(Material.EXPERIENCE_BOTTLE, 32);
                        final ItemStack i9 = new ItemStack(Material.SHULKER_SHELL);
                        final ItemMeta im1 = i1.getItemMeta();
                        final ItemMeta im2 = i2.getItemMeta();
                        final ItemMeta im3 = i3.getItemMeta();
                        final ItemMeta im4 = i4.getItemMeta();
                        final ItemMeta im5 = i5.getItemMeta();
                        final ItemMeta im6 = i6.getItemMeta();
                        final ItemMeta im7 = i7.getItemMeta();
                        final ItemMeta im8 = i8.getItemMeta();
                        final ItemMeta im9 = i9.getItemMeta();
                        final List<String> lore = new ArrayList<String>();
                        lore.add(ChatColor.RED + "You cannot PvP whilst flying, but people can attack you. Be warned!");
                        im6.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_PLACED_ON });
                        im1.setDisplayName(ChatColor.AQUA + "Faction TP - " + ChatColor.YELLOW + "5 FP");
                        im2.setDisplayName(ChatColor.AQUA + "Saturation for 5 seconds - " + ChatColor.YELLOW + "10 FP");
                        im3.setDisplayName(ChatColor.AQUA + "Speed II for 1000 ticks - " + ChatColor.YELLOW + "25 FP");
                        im4.setDisplayName(ChatColor.AQUA + "Regeneration IV for 1000 ticks - " + ChatColor.YELLOW + "50 FP");
                        im5.setDisplayName(ChatColor.AQUA + "30 Diamonds - " + ChatColor.YELLOW + "100 FP");
                        im6.setDisplayName(ChatColor.AQUA + "Superior Netherite Chestplate - " + ChatColor.YELLOW + "150 FP");
                        im7.setDisplayName(ChatColor.AQUA + "Saddle - " + ChatColor.YELLOW + "6 FP");
                        im8.setDisplayName(ChatColor.AQUA + "32 Experience Bottles - " + ChatColor.YELLOW + "9 FP");
                        im9.setDisplayName(ChatColor.AQUA + "Fly Mode for 5000 ticks - " + ChatColor.YELLOW + "20 FP");
                        im9.setLore((List)lore);
                        im6.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                        im3.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                        i1.setItemMeta(im1);
                        i2.setItemMeta(im2);
                        i3.setItemMeta(im3);
                        i4.setItemMeta(im4);
                        i5.setItemMeta(im5);
                        i6.setItemMeta(im6);
                        i7.setItemMeta(im7);
                        i8.setItemMeta(im8);
                        i9.setItemMeta(im9);
                        final Inventory in = Bukkit.createInventory((InventoryHolder)null, 18, "Perks Selection Menu");
                        in.setItem(0, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(1, i1);
                        in.setItem(2, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(3, i7);
                        in.setItem(4, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(5, i8);
                        in.setItem(6, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(7, i2);
                        in.setItem(8, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(9, i3);
                        in.setItem(10, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(11, i4);
                        in.setItem(12, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(13, i5);
                        in.setItem(14, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(15, i6);
                        in.setItem(16, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                        in.setItem(17, i9);
                        p.openInventory(in);
                        Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + ";perkmenu", (Object)true);
                        Main.this.saveConfig();
                    }
                    else {
                        sender.sendMessage("You are not faction leader.");
                    }
                    Main.this.saveConfig();
                }
                else if (args[0].toLowerCase().equals("leave")) {
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    if (p.getUniqueId().equals(fac2.leader)) {
                        sender.sendMessage(ChatColor.RED + "You cannot leave a faction when you are leader. Disband instead.");
                        return true;
                    }
                    if (!Main.this.getConfig().contains(p.getUniqueId().toString())) {
                        sender.sendMessage("You are not in a faction.");
                        return true;
                    }
                    Main.this.getConfig().set(p.getUniqueId().toString(), (Object)null);
                    Main.this.saveConfig();
                    sender.sendMessage("You have left the faction.");
                }
                else if (args[0].toLowerCase().equals("disband")) {
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final List<String> allies2 = fac2.allies;
                    for (final String string : allies2) {
                        if (!string.equals("Q")) {
                            final Faction fac3 = Faction.deserialize(Main.this.getConfig().getString(string));
                            fac3.removeAlly(fac2.name);
                            Main.this.getConfig().set(fac3.name, (Object)fac3.serialize());
                            Main.this.saveConfig();
                        }
                    }
                    if (p.getUniqueId().equals(fac2.leader)) {
                        Main.this.getConfig().set(fac2.name, (Object)null);
                        sender.sendMessage(ChatColor.GREEN + "Your faction has been disbanded.");
                        Main.this.saveConfig();
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "You are not the faction's leader.");
                    }
                }
                else if (args[0].toLowerCase().equals("list")) {
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    sender.sendMessage(ChatColor.GREEN + "Members in faction " + ChatColor.BLUE + fac2.name.split("=")[0] + ChatColor.GREEN + ":\n");
                    OfflinePlayer[] offlinePlayers;
                    for (int length = (offlinePlayers = Bukkit.getOfflinePlayers()).length, j = 0; j < length; ++j) {
                        final OfflinePlayer op = offlinePlayers[j];
                        if (Main.this.getConfig().getString(p.getUniqueId().toString()).equals(Main.this.getConfig().getString(op.getUniqueId().toString()))) {
                            sender.sendMessage(ChatColor.GOLD + op.getName());
                        }
                    }
                    sender.sendMessage(ChatColor.RED + "Faction Power: " + ChatColor.AQUA + String.valueOf(fac2.power));
                    final List<String> allies2 = new ArrayList<String>();
                    final List<String> wars2 = new ArrayList<String>();
                    for (final String string2 : fac2.allies) {
                        if (!string2.equals("Q")) {
                            allies2.add(string2.split("=")[0]);
                        }
                    }
                    for (final String string2 : fac2.wars) {
                        if (!string2.equals("Q")) {
                            wars2.add(string2.split("=")[0]);
                        }
                    }
                    sender.sendMessage(ChatColor.DARK_PURPLE + "Allies: " + ChatColor.AQUA + String.join(", ", allies2));
                    sender.sendMessage(ChatColor.DARK_PURPLE + "Wars: " + ChatColor.RED + String.join(", ", wars2));
                }
                else if (args[0].toLowerCase().equals("ally")) {
                    if (Main.this.getConfig().get("experimental_features") == null) {
                        sender.sendMessage(ChatColor.RED + "This is an experimental feature which can be buggy and is in early stages of development.\nThis has not been enabled by the server operators.");
                        return false;
                    }
                    if (args[1].equals("accept")) {
                        final Player pla = Bukkit.getPlayer(args[2]);
                        Main.this.getLogger().info(Main.this.getConfig().getString(Main.this.getConfig().getString(pla.getUniqueId().toString())));
                        final Faction fac4 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(pla.getUniqueId().toString())));
                        final Faction fac = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                        if (Main.this.getConfig().getString(String.valueOf(fac4.name) + "/ALLYREQ/" + fac.name) != null) {
                            fac4.allies.add(fac.name);
                            fac.allies.add(fac4.name);
                            Main.this.getConfig().set(String.valueOf(fac4.name) + "/ALLYREQ/" + fac.name, (Object)null);
                            Main.this.getConfig().set(fac.name, (Object)fac.serialize());
                            Main.this.getConfig().set(fac4.name, (Object)fac4.serialize());
                            sender.sendMessage(ChatColor.GREEN + "This faction has been added to your ally list.");
                            try {
                                Bukkit.getPlayer(fac4.leader).sendMessage(ChatColor.GREEN + "Your request was accepted!");
                            }
                            catch (Exception ex) {}
                            Main.this.saveConfig();
                            return true;
                        }
                    }
                    if (args[1].equals("deny")) {
                        final Player pla = Bukkit.getPlayer(args[2]);
                        Main.this.getLogger().info(Main.this.getConfig().getString(Main.this.getConfig().getString(pla.getUniqueId().toString())));
                        final Faction fac4 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(pla.getUniqueId().toString())));
                        final Faction fac = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                        if (Main.this.getConfig().getString(String.valueOf(fac4.name) + "/ALLYREQ/" + fac.name) != null) {
                            Main.this.getConfig().set(String.valueOf(fac4.name) + "/ALLYREQ/" + fac.name, (Object)null);
                            sender.sendMessage(ChatColor.GREEN + "This faction has not been added to your ally list.");
                            try {
                                Bukkit.getPlayer(fac4.leader).sendMessage(ChatColor.RED + "Your request was rejected.");
                            }
                            catch (Exception ex2) {}
                            Main.this.saveConfig();
                            return true;
                        }
                    }
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final Faction fac5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    if (fac2.leader.equals(fac5.leader)) {
                        sender.sendMessage(ChatColor.RED + "You cannot ally yourself!");
                        return true;
                    }
                    if (fac2.allies.contains(fac5.name)) {
                        sender.sendMessage(ChatColor.RED + "This Faction is already in your ally list!");
                        return true;
                    }
                    if (p.getUniqueId().equals(fac2.leader)) {
                        Main.this.getConfig().set(String.valueOf(fac2.name) + "/ALLYREQ/" + fac5.name, (Object)true);
                        Main.this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You sent " + fac5.name.split("=")[0] + " an ally request.");
                        Bukkit.getPlayer(fac5.leader).sendMessage(ChatColor.GREEN + "You were sent an ally request from " + fac2.name.split("=")[0] + ". Do /f ally accept " + Bukkit.getPlayer(fac2.leader).getName() + ".");
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "You are not faction leader.");
                    }
                }
                else if (args[0].toLowerCase().equals("deally")) {
                    if (Main.this.getConfig().get("experimental_features") == null) {
                        sender.sendMessage(ChatColor.RED + "This is an experimental feature which can be buggy and is in early stages of development\nThis has not been enabled by the server operators.");
                        return false;
                    }
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final Faction fac5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    if (fac2.leader.equals(fac5.leader)) {
                        sender.sendMessage(ChatColor.RED + "You cannot deally yourself!");
                        return true;
                    }
                    if (!fac2.allies.contains(fac5.name)) {
                        sender.sendMessage(ChatColor.RED + "This Faction is already not in your ally list!");
                        return true;
                    }
                    if (p.getUniqueId().equals(fac2.leader)) {
                        fac2.allies.remove(fac5.name);
                        fac5.allies.remove(fac2.name);
                        Main.this.getConfig().set(fac2.name, (Object)fac2.serialize());
                        Main.this.getConfig().set(fac5.name, (Object)fac5.serialize());
                        Main.this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You removed " + fac5.name.split("=")[0] + " from your ally list. You will now be able to damage these players.");
                    }
                    if (Bukkit.getOfflinePlayer(fac5.leader).isOnline()) {
                        Bukkit.getPlayer(fac5.leader).sendMessage(ChatColor.YELLOW + "Your faction has been declared neutral by " + fac2.name.split("=")[0] + ".");
                    }
                }
                else if (args[0].equalsIgnoreCase("declarewar")) {
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final Faction fac5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    if (fac2.leader.equals(fac5.leader)) {
                        sender.sendMessage(ChatColor.RED + "You cannot declare war on yourself!");
                        return true;
                    }
                    if (fac2.allies.contains(fac5.name)) {
                        sender.sendMessage(ChatColor.RED + "You are already at war with them!");
                        return true;
                    }
                    if (!p.getUniqueId().equals(fac2.leader)) {
                        sender.sendMessage(ChatColor.RED + "You are not faction leader.");
                        return true;
                    }
                    if (fac2.allies.contains(fac5.name)) {
                        sender.sendMessage(ChatColor.RED + "You cannot declare war on an ally.");
                        return true;
                    }
                    if (fac2.wars.contains(fac5.name)) {
                        sender.sendMessage(ChatColor.RED + "You are already at war with this faction.");
                        return true;
                    }
                    fac2.wars.add(fac5.name);
                    fac5.wars.add(fac2.name);
                    Main.this.getConfig().set(fac2.name, (Object)fac2.serialize());
                    Main.this.getConfig().set(fac5.name, (Object)fac5.serialize());
                    Main.this.saveConfig();
                    try {
                        Bukkit.getPlayer(fac2.leader).sendMessage(ChatColor.GREEN + "You declared war on " + fac5.name.split("=")[0] + ".");
                    }
                    catch (Exception ex3) {}
                    try {
                        Bukkit.getPlayer(fac5.leader).sendMessage(ChatColor.RED + "You were declared war on by " + fac2.name.split("=")[0] + ".");
                    }
                    catch (Exception ex4) {}
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final Faction fac5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    if (!p.getUniqueId().equals(fac2.leader)) {
                        sender.sendMessage(ChatColor.RED + "You are not faction leader.");
                        return true;
                    }
                    if (fac2.equals(fac5)) {
                        Main.this.getConfig().set(Bukkit.getPlayer(args[1]).getUniqueId().toString(), (Object)null);
                        Main.this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You have kicked " + Bukkit.getPlayer(args[1]).getName() + " from your faction.");
                    }
                }
                else {
                    if (!args[0].equalsIgnoreCase("endwar")) {
                        throw new Exception();
                    }
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    final Faction fac5 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    if (!p.getUniqueId().equals(fac2.leader)) {
                        sender.sendMessage(ChatColor.RED + "You are not faction leader.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("accept")) {
                        if (Main.this.getConfig().getBoolean(String.valueOf(fac2.name) + "/WAR/" + fac5.name)) {
                            fac2.wars.remove(fac5.name);
                            fac5.wars.remove(fac2.name);
                            Main.this.getConfig().set(fac2.name, (Object)fac2.serialize());
                            Main.this.getConfig().set(fac5.name, (Object)fac5.serialize());
                            Main.this.getConfig().set(String.valueOf(fac2.name) + "/WAR/" + fac5.name, (Object)null);
                            sender.sendMessage(ChatColor.RED + "The war... is over.");
                            Main.this.saveConfig();
                            return true;
                        }
                        sender.sendMessage(ChatColor.RED + "You are not at war with this faction.");
                        return true;
                    }
                    else {
                        if (args[0].equalsIgnoreCase("deny")) {
                            Bukkit.getPlayer(args[1]).sendMessage(ChatColor.RED + "The war continues. Blood for the Blood God.");
                            Main.this.getConfig().set(String.valueOf(fac2.name) + "/WAR/" + fac5.name, (Object)null);
                            sender.sendMessage(ChatColor.RED + "The war continues. Blood for the Blood God.");
                            Main.this.saveConfig();
                        }
                        if (fac2.allies.contains(fac5.name)) {
                            sender.sendMessage(ChatColor.RED + "You cannot undeclare war on an ally.");
                            return true;
                        }
                        Main.this.getConfig().set(String.valueOf(fac2.name) + "/WAR/" + fac5.name, (Object)true);
                        Main.this.saveConfig();
                        try {
                            Bukkit.getPlayer(fac2.leader).sendMessage(ChatColor.GREEN + "You asked to end the war on " + fac5.name.split("=")[0] + ".");
                        }
                        catch (Exception ex5) {}
                        try {
                            Bukkit.getPlayer(fac5.leader).sendMessage(ChatColor.GREEN + "You were asked to end the war on " + fac2.name.split("=")[0] + " by doing /f endwar accept *NAME*.");
                        }
                        catch (Exception ex6) {}
                    }
                }
            }
            catch (Exception e2) {
                sender.sendMessage(ChatColor.RED + "That is an invalid action.");
            }
            return true;
        }
    }
    
    public class AcceptListener implements CommandExecutor
    {
        public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
            final Player p = (Player)sender;
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You did not put the inviter as an argument.");
                return true;
            }
            if (Main.this.getConfig().contains(p.getUniqueId().toString())) {
                sender.sendMessage(ChatColor.RED + "You are already in a faction.");
                final Player pl = Bukkit.getPlayer(args[0]);
                Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString(), (Object)null);
                return false;
            }
            final Player pl = Bukkit.getPlayer(args[0]);
            final String invcheck = Main.this.getConfig().getString(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString());
            if (invcheck == null) {
                sender.sendMessage(ChatColor.RED + "You did not recieve an invite from this player.");
                return true;
            }
            final Faction fac = Faction.deserialize(Main.this.getConfig().getString(invcheck));
            Main.this.getConfig().set(p.getUniqueId().toString(), (Object)fac.name);
            Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString(), (Object)null);
            Main.this.saveConfig();
            sender.sendMessage(ChatColor.BLUE + "You have been added to the faction " + fac.name.split("=")[0] + ".");
            Main.this.saveConfig();
            return true;
        }
    }
    
    public class DenyListener implements CommandExecutor
    {
        public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
            final Player p = (Player)sender;
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You did not put the inviter as an argument.");
                return true;
            }
            final Player pl = Bukkit.getPlayer(args[0]);
            final String invcheck = Main.this.getConfig().getString(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString());
            if (invcheck == null) {
                sender.sendMessage(ChatColor.RED + "You did not recieve an invite from this player.");
                return true;
            }
            final Faction fac = Faction.deserialize(Main.this.getConfig().getString(invcheck));
            Main.this.getConfig().set(String.valueOf(p.getUniqueId().toString()) + "=INV=" + pl.getUniqueId().toString(), (Object)null);
            Main.this.saveConfig();
            sender.sendMessage(ChatColor.BLUE + "You have declined the request to the faction " + fac.name.split("=")[0] + ".");
            Main.this.saveConfig();
            return true;
        }
    }
    
    public class FacAdminExecutor implements CommandExecutor
    {
        public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
            try {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You do not have operator and therefore cannot use this command.");
                    return false;
                }
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "That is an invalid action.");
                    return false;
                }
                if (args[0].toLowerCase().toLowerCase().toLowerCase().equals("setpower")) {
                    final Player p = Bukkit.getPlayer(args[1]);
                    if (Main.this.getConfig().getString(p.getUniqueId().toString()) == null) {
                        sender.sendMessage(ChatColor.RED + "Player is not in a faction.");
                        return false;
                    }
                    final Faction fac = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(p.getUniqueId().toString())));
                    fac.setPower(Integer.parseInt(args[2]));
                    Main.this.getConfig().set(fac.name, (Object)fac.serialize());
                    sender.sendMessage(ChatColor.GREEN + "Faction Power of " + fac.name.split("=")[0] + " is now set to " + args[2] + ".");
                    Main.this.saveConfig();
                }
                else if (args[0].toLowerCase().toLowerCase().toLowerCase().equals("disband")) {
                    if (Main.this.getConfig().get(Bukkit.getPlayer(args[1]).getUniqueId().toString()) == null) {
                        sender.sendMessage(ChatColor.RED + "Player is not in a faction.");
                        return false;
                    }
                    final Faction fac2 = Faction.deserialize(Main.this.getConfig().getString(Main.this.getConfig().getString(Bukkit.getPlayer(args[1]).getUniqueId().toString())));
                    Main.this.getConfig().set(fac2.name, (Object)null);
                    Main.this.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Faction " + fac2.name + " has now been disbanded.");
                }
                else if (args[0].toLowerCase().toLowerCase().equals("setid")) {
                    final Player p = (Player)sender;
                    final ItemStack i = p.getInventory().getItemInMainHand();
                    if (i == null) {
                        sender.sendMessage(ChatColor.RED + "You are not holding an item.");
                    }
                    final ItemMeta im = i.getItemMeta();
                    final List<String> lore = new ArrayList<String>();
                    lore.add(ChatColor.DARK_GRAY + "CUSTOM_ID:" + args[1]);
                    sender.sendMessage(ChatColor.GREEN + "ID of item in hand has been set to " + args[1] + ".");
                    im.setLore((List)lore);
                    i.setItemMeta(im);
                    p.getInventory().setItemInMainHand(i);
                }
                else if (args[0].toLowerCase().equals("experimental")) {
                    if (Main.this.getConfig().get("experimental_features") != null) {
                        Main.this.getConfig().set("experimental_features", (Object)null);
                        sender.sendMessage(ChatColor.GREEN + "Experimental features disabled.");
                    }
                    else {
                        Main.this.getConfig().set("experimental_features", (Object)true);
                        sender.sendMessage(ChatColor.GREEN + "Experimental features enabled.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "That is an invalid action.");
                }
            }
            catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "That is an invalid action.");
            }
            return true;
        }
    }
}
