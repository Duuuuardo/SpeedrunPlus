package com.duuuuardo.mhresources;

import java.util.function.Consumer;
import org.bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MHCountdown implements Runnable {
  private JavaPlugin plugin;
  private Integer assignedTaskId;
  private int seconds;
  private int secondsLeft;
  private Consumer<MHCountdown> everySecond;
  private Runnable afterTimer;

  public MHCountdown(
      JavaPlugin plugin, int seconds, Runnable afterTimer, Consumer<MHCountdown> everySecond) {
    this.seconds = seconds;
    this.plugin = plugin;
    this.secondsLeft = seconds;
    this.afterTimer = afterTimer;
    this.everySecond = everySecond;
  }

  @Override
  public void run() {
    if (secondsLeft < 1) {
      afterTimer.run();

      if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
      return;
    }

    everySecond.accept(this);

    secondsLeft--;
  }

  public int getTotalSeconds() {
    return seconds;
  }

  public int getSecondsLeft() {
    return secondsLeft;
  }

  public void scheduleTimer() {
    this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
  }
}
