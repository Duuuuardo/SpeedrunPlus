package com.duuuuardo.mhresources;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CompassInventory {

  public static final String INVENTORY_NAME = "Selecione um jogador";

  Inventory inv;
  Main main;

  public CompassInventory(Main main) {
    this.main = main;
    inv = Bukkit.createInventory(null, 9, INVENTORY_NAME);
    int pos = 0;
    for (Player runner : Bukkit.getOnlinePlayers()) {
      if (runner == null) continue;
      ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
      SkullMeta meta = (SkullMeta) stack.getItemMeta();
      meta.setOwningPlayer(runner);
      meta.setDisplayName(runner.getName());
      stack.setItemMeta(meta);

      inv.setItem(pos, stack);
      pos++;
    }
  }

  public Inventory getInventory() {
    return inv;
  }

  public void DisplayToPlayer(Player player) {
    player.openInventory(inv);
  }
}
