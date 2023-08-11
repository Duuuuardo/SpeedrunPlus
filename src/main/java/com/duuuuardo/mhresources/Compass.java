package com.duuuuardo.mhresources;

import java.util.Map;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass {
  public static void UpdateCompass(Main main) {
    for (Map.Entry<String, String> i : main.targets.entrySet()) {
      Player hunter = Bukkit.getPlayer(i.getKey());
      Player target = Bukkit.getPlayer(i.getValue());
      if (hunter == null || target == null) {
        continue;
      }

      PlayerInventory inv = hunter.getInventory();

      if (hunter.getWorld().getEnvironment() != target.getWorld().getEnvironment()) {
        Location loc = main.portals.get(target.getName());
        if (loc != null) {
          hunter.setCompassTarget(loc);
        }
        for (int j = 0; j < inv.getSize(); j++) {
          ItemStack stack = inv.getItem(j);
          if (stack == null) continue;
          if (stack.getType() != Material.COMPASS) continue;

          stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

          ItemMeta meta = stack.getItemMeta();
          meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
          stack.setItemMeta(meta);
        }
      } else {
        for (int j = 0; j < inv.getSize(); j++) {
          ItemStack stack = inv.getItem(j);
          if (stack == null) continue;
          if (stack.getType() != Material.COMPASS) continue;

          stack.removeEnchantment(Enchantment.DAMAGE_ALL);
        }

        hunter.setCompassTarget(target.getLocation());
      }
    }
  }
}
