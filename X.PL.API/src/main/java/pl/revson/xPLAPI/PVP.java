package pl.revson.xPLAPI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class PVP extends JavaPlugin implements Listener {

    private final Set<Player> pvpPlayers = new HashSet<>(); // Zestaw graczy w trybie PvP

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void activatePvP(Player player) {
        pvpPlayers.add(player);
        player.sendMessage("§8[§6§lX§f§l.PL§8] §aJesteś teraz w trybie PvP!"); // Wiadomość o aktywowaniu PvP
    }

    private void deactivatePvP(Player player) {
        pvpPlayers.remove(player);
        player.sendMessage("§8[§6§lX§f§l.PL§8] §cNie jesteś już w trybie PvP!"); // Wiadomość o dezaktywacji PvP
    }

    private boolean hasPvPSet(Player player) {
        return player.getInventory().getHelmet() != null &&
                player.getInventory().getChestplate() != null &&
                player.getInventory().getLeggings() != null &&
                player.getInventory().getBoots() != null &&
                player.getInventory().getItem(0) != null; // Sprawdza, czy wszystkie elementy zestawu są w ekwipunku
    }

    private void givePvPSet(Player player) {
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

        // Ustawienie ekwipunku gracza
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(0, sword); // Ustawienie miecza w pierwszym slocie

        // Aktywacja trybu PvP
        activatePvP(player);

        player.sendMessage("§8[§6§lX§f§l.PL§8] §aOtrzymałeś zestaw PvP!"); // Informacja o dodaniu zestawu
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Tutaj możesz przyznać graczowi zestaw PvP, jeśli jest to wymagane na przykład:
        // givePvPSet(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        deactivatePvP(player); // Dezaktywacja trybu PvP podczas wyjścia z gry
    }


    //Dokończmy tę logikę, aby zapewnić, że obie strony (atakujący i ofiara) będą odpowiednio informowane o statusie trybu PvP, a także aby kod był poprawnie zakończony.

    //Oto dokończona wersja metody onEntityDamage:

    //java
    //Copy
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            // Sprawdź, czy którykolwiek z graczy nie jest w trybie PvP
            if (!pvpPlayers.contains(attacker) || !pvpPlayers.contains(victim)) {
                event.setCancelled(true); // Zablokuj obrażenia
                if (!pvpPlayers.contains(attacker)) {
                    attacker.sendMessage("§cNie możesz atakować, jeśli nie jesteś w trybie PvP!"); // Ostrzeżenie dla atakującego
                }
                if (!pvpPlayers.contains(victim)) {
                    victim.sendMessage("§cNie możesz być atakowany, jeśli nie jesteś w trybie PvP!"); // Ostrzeżenie dla ofiary
                }
            }
        }
    }
}