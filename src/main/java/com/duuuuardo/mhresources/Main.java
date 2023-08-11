package com.duuuuardo.mhresources;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
  public Logger logger;
  public HashMap<String, String> targets = new HashMap<String, String>();
  public HashMap<String, Location> portals = new HashMap<String, Location>();
  public HashMap<String, Integer[]> counters = new HashMap<String, Integer[]>();
  public MHCommands commands;
  public Boolean started = false;
  public long startedAt;
  BossBar bossBar =
      Bukkit.createBossBar(ChatColor.RED + "Aguardando...", BarColor.RED, BarStyle.SEGMENTED_10);

  @Override
  public void onEnable() {
    logger = getLogger();
    logger.info("Plugin iniciado!");
    logger.info("Sucumba coqero!");
    Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    counters.put("Duuuuardo", new Integer[] {0, 0});
    counters.put("Coqero", new Integer[] {0, 0});
    commands = new MHCommands(this);
    for (String command : MHCommands.registeredCommands) {
      this.getCommand(command).setExecutor(commands);
    }
  }

  public void onDisable() {
    logger.info("Plugin desativado.");
  }
}
