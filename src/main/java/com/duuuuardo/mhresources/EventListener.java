package com.duuuuardo.mhresources;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class EventListener implements Listener {
  Main main;
  int compassTask = -1;
  MHScoreboard scoreboard;

  public EventListener(Main main) {
    this.main = main;
    this.scoreboard = new MHScoreboard(main);
  }

  @EventHandler
  public void onAutocomplete(TabCompleteEvent event) {
    String buffer = event.getBuffer();
    if (!buffer.startsWith("/")) return;
    String[] args = buffer.split(" ");

    List<String> completions = main.commands.getCompletions(args, event.getCompletions());

    event.setCompletions(completions);
  }

  @EventHandler
  public void onPlayerEnterPortal(PlayerPortalEvent event) {
    main.portals.put(event.getPlayer().getName(), event.getFrom());
  }

  @EventHandler
  public void movement(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    Double xTo = event.getTo().getX();
    Double xFrom = event.getFrom().getX();
    Double yTo = event.getTo().getY();
    Double yFrom = event.getFrom().getY();
    Double zTo = event.getTo().getZ();
    Double zFrom = event.getFrom().getZ();
    if (event.getTo().locToBlock(xTo) != event.getFrom().locToBlock(xFrom)
        || event.getTo().locToBlock(zTo) != event.getFrom().locToBlock(zFrom)
        || event.getTo().locToBlock(yTo) != event.getFrom().locToBlock(yFrom)) {
      if (player.getGameMode() == GameMode.SURVIVAL && !main.started) {
        player.teleport(event.getFrom());
      }
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();

    World world = main.getServer().getWorld("world");
    Location spawnpoint = world.getSpawnLocation();

    int x = spawnpoint.getBlockX() + randInt(-500, 500);
    int z = spawnpoint.getBlockZ() + randInt(-500, 500);
    int y = world.getHighestBlockYAt(x, z);

    Location location = new Location(world, x, y, z);

    if (main.started) {
      event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS, 1));
    }
    player.teleport(location);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    String name = event.getEntity().getName();
    Player killer = event.getEntity().getKiller();
    event.getDrops().removeIf(i -> i.getType() == Material.COMPASS);

    if (killer != null) {
      Integer[] killer_kd = main.counters.get(killer.getName());
      Object killer_kills = Array.get(killer_kd, 0);
      Object killer_deaths = Array.get(killer_kd, 1);
      int kk = (Integer) killer_kills;
      int kd = (Integer) killer_deaths;

      main.counters.put(killer.getName(), new Integer[] {kk + 1, kd});
    }

    Integer[] player_kd = main.counters.get(name);
    Object player_kills = Array.get(player_kd, 0);
    Object player_deaths = Array.get(player_kd, 1);

    int pk = (Integer) player_kills;
    int pd = (Integer) player_deaths;

    main.counters.put(name, new Integer[] {pk, pd + 1});
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = (Player) event.getPlayer();
    BukkitScheduler scheduler = Bukkit.getScheduler();
    main.bossBar.addPlayer(player);
    ItemStack compass = new ItemStack(Material.COMPASS);

    if (!player.getInventory().contains(compass.getType())) {
      player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
    }

    compassTask =
        scheduler.scheduleSyncRepeatingTask(
            main,
            new Runnable() {
              public void run() {
                Compass.UpdateCompass(main);
                scoreboard.UpdateScoreboard(player);
              }
            },
            0L,
            20L);
  }

  @EventHandler
  public void onClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    ItemStack item = player.getEquipment().getItemInMainHand();

    if (item.getType() == Material.COMPASS) {
      CompassInventory inv = new CompassInventory(main);
      inv.DisplayToPlayer(player);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Player hunter = (Player) event.getWhoClicked();

    ItemStack clickedHead = event.getCurrentItem();
    if (event.getView().getTitle().equals(CompassInventory.INVENTORY_NAME)) {

      if (clickedHead == null || clickedHead.getType() != Material.PLAYER_HEAD) {
        main.logger.warning("O item que você clicou não é uma cabeça de player.");
        event.setCancelled(true);
        return;
      }
      if (!clickedHead.hasItemMeta()) {
        main.logger.warning("Este item não tem metadados.");
        hunter.sendMessage("Este item não tem metadados");
        event.setCancelled(true);
        return;
      }
      ItemMeta itemmeta = clickedHead.getItemMeta();
      if (!(itemmeta instanceof SkullMeta)) {
        main.logger.warning("Aparentemente o item não é uma instancia de SkullMeta");
        main.logger.info(itemmeta.getClass().toString());
        hunter.sendMessage("Aparentemente o item não é uma instancia de SkullMeta");
        event.setCancelled(true);
        return;
      }
      SkullMeta meta = (SkullMeta) itemmeta;
      OfflinePlayer target = meta.getOwningPlayer();
      String targetName = target.getName();
      if (targetName == null) {
        targetName = meta.getDisplayName();
        main.logger.info("Este usuario está offline: " + targetName);
      }
      main.targets.put(hunter.getName(), targetName);
      event.setCancelled(true);
      hunter.closeInventory();
      hunter.sendMessage("Rastreando " + targetName);
    }
  }

  public static int randInt(int min, int max) {
    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
  }
}
