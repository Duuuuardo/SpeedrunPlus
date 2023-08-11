package com.duuuuardo.mhresources;

import java.util.List;
import org.bukkit.*;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class MHCommands implements CommandExecutor {
  private final Main main;
  public static final String[] registeredCommands = {
    "start",
  };

  public MHCommands(Main main) {
    this.main = main;
  }

  public List<String> getCompletions(String[] args, List<String> existingCompletions) {
    switch (args[0]) {
      case "/start":
      default:
        return existingCompletions;
    }
  }

  public boolean onCommand(
      CommandSender commandSender, Command command, String label, String[] args) {
    if ("start".equals(label)) {
      main.started = false;
      for (Player p : Bukkit.getOnlinePlayers()) {
        main.bossBar.setProgress(1);
        main.bossBar.addPlayer(p);
        MHCountdown timer =
            new MHCountdown(
                main,
                10,
                () -> {
                  String text = ChatColor.translateAlternateColorCodes('&', "&c&nSe Matem");
                  p.sendTitle(text, "", 10, 40, 3);
                  main.startedAt = System.currentTimeMillis();
                  main.started = true;
                  p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                  main.bossBar.removeAll();
                  FireworkEffect.Builder builder = FireworkEffect.builder();
                  FireworkEffect effect =
                      builder
                          .flicker(true)
                          .trail(true)
                          .with(FireworkEffect.Type.BURST)
                          .withColor(Color.RED)
                          .withFade(Color.RED)
                          .build();
                  Firework firework =
                      p.getLocation().getWorld().spawn(p.getLocation(), Firework.class);
                  FireworkMeta fwm = firework.getFireworkMeta();
                  fwm.clearEffects();
                  fwm.addEffect(effect);
                  firework.setFireworkMeta(fwm);
                },
                (t) -> {
                  Integer left = t.getSecondsLeft();
                  String text;

                  if (left < 4) {
                    text = ChatColor.translateAlternateColorCodes('&', "&a" + left);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                    main.bossBar.setColor(BarColor.GREEN);
                    main.bossBar.setTitle(ChatColor.GREEN + "Aguardando...");
                  } else {
                    text = ChatColor.translateAlternateColorCodes('&', "&f" + left);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10, 1);
                  }
                  Float progress = left.floatValue() / 10;
                  main.bossBar.setProgress(progress);
                  p.sendTitle(text, "", 10, 40, 10);
                });
        timer.scheduleTimer();
      }
    }
    return true;
  }
}
