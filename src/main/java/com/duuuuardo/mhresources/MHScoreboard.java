package com.duuuuardo.mhresources;

import java.lang.reflect.Array;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class MHScoreboard {
  Main main;

  public MHScoreboard(Main main) {
    this.main = main;
  }

  public void UpdateScoreboard(Player player) {
    if (player.getScoreboard().equals(main.getServer().getScoreboardManager().getMainScoreboard()))
      player.setScoreboard(main.getServer().getScoreboardManager().getNewScoreboard());
    Scoreboard s = player.getScoreboard();
    Objective o =
        s.getObjective(player.getName()) == null
            ? s.registerNewObjective(player.getName(), "dummy")
            : s.getObjective(player.getName());

    o.setDisplayName(" §l Duelo Lendario! §r");

    String timer;
    if (main.started) {
      long elapsedTime = System.currentTimeMillis() - main.startedAt;
      long elapsedSeconds = elapsedTime / 1000;
      long secondsDisplay = elapsedSeconds % 60;
      long elapsedMinutes = elapsedSeconds / 60;
      long minutesDisplay = elapsedMinutes % 60;
      long elapsedHours = elapsedMinutes / 60;
      long hoursDisplay = elapsedHours % 60;
      String minutes;
      String formatedTime;
      String seconds;
      String hours;

      if (minutesDisplay < 10) {
        minutes = "0" + minutesDisplay;
      } else {
        minutes = "" + minutesDisplay;
      }

      if (secondsDisplay < 10) {
        seconds = "0" + secondsDisplay;
      } else {
        seconds = "" + secondsDisplay;
      }

      if (hoursDisplay < 10) {
        hours = "0" + hoursDisplay;
      } else {
        hours = "" + hoursDisplay;
      }
      if (elapsedMinutes >= 60) {
        formatedTime = hours + ":" + minutes + ":" + seconds;
      } else {
        formatedTime = minutes + ":" + seconds;
      }
      timer = String.format("§a§lIniciado há: §r%s", formatedTime);
    } else {
      timer = String.format("       §a§lAGUARDANDO");
    }

    replaceScore(o, 5, timer);
    replaceScore(o, 4, "");
    replaceScore(o, 3, "               K/D");
    Integer[] coqero = main.counters.get("Coqero");
    Object coqero_kills = Array.get(coqero, 0);
    Object coqero_deaths = Array.get(coqero, 1);
    String coqero_kd = String.format(" §e§lCoqero: §r%o/%o", coqero_kills, coqero_deaths);
    replaceScore(o, 2, coqero_kd);

    Integer[] duuuuardo = main.counters.get("Duuuuardo");
    Object duuuuardo_kills = Array.get(duuuuardo, 0);
    Object duuuuardo_deaths = Array.get(duuuuardo, 1);
    String duardo_kd = String.format(" §e§lDuardo: §r%o/%o", duuuuardo_kills, duuuuardo_deaths);
    replaceScore(o, 1, duardo_kd);
    if (o.getDisplaySlot() != DisplaySlot.SIDEBAR) o.setDisplaySlot(DisplaySlot.SIDEBAR);
    player.setScoreboard(s);
  }

  public static String getEntryFromScore(Objective o, int score) {
    if (o == null) return null;
    if (!hasScoreTaken(o, score)) return null;
    for (String s : o.getScoreboard().getEntries()) {
      if (o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
    }
    return null;
  }

  public static boolean hasScoreTaken(Objective o, int score) {
    for (String s : o.getScoreboard().getEntries()) {
      if (o.getScore(s).getScore() == score) return true;
    }
    return false;
  }

  public static void replaceScore(Objective o, int score, String name) {
    if (hasScoreTaken(o, score)) {
      if (getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
      if (!(getEntryFromScore(o, score).equalsIgnoreCase(name)))
        o.getScoreboard().resetScores(getEntryFromScore(o, score));
    }
    o.getScore(name).setScore(score);
  }
}
