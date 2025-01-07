package pl.revson.xPLAPI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PVPBlocker implements Listener {

    // Event, który jest wywoływany, gdy gracz próbuje wyrzucić przedmiot
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        // Sprawdzenie, czy wyrzucany przedmiot jest częścią zestawu PVP
        if (isPvPItem(item)) {
            // Zablokowanie wyrzucania przedmiotu
            event.setCancelled(true);
            player.sendMessage("§8[§6§lX§f§l.PL§8] §cNie możesz wyrzucić elementu zestawu PVP.");
        }
    }

    // Sprawdzenie, czy przedmiot jest częścią zestawu PVP
    private boolean isPvPItem(ItemStack item) {
        if (item == null) return false;

        // Sprawdzenie, czy przedmiot jest mieczem PVP lub częścią zestawu diamentowej zbroi
        return isPvPSword(item) || isPvPArmor(item);
    }

    // Sprawdzenie, czy przedmiot to miecz PVP
    private boolean isPvPSword(ItemStack item) {
        if (item.getType() == Material.DIAMOND_SWORD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                // Zakładając, że miecz ma unikalną nazwę (np. "§6Miecz PVP")
                return meta.getDisplayName().equals("§6Miecz PVP");
            }
        }
        return false;
    }

    // Sprawdzenie, czy przedmiot to element diamentowej zbroi (część zestawu PVP)
    private boolean isPvPArmor(ItemStack item) {
        return item.getType() == Material.DIAMOND_HELMET ||
                item.getType() == Material.DIAMOND_CHESTPLATE ||
                item.getType() == Material.DIAMOND_LEGGINGS ||
                item.getType() == Material.DIAMOND_BOOTS;
    }
}