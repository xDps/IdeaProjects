package pl.revson.xPLAPI;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class PVPColdown {

    private final Map<Player, Long> playerCooldowns = new HashMap<>(); // Mapa przechowująca czas ostatniego użycia

    // Metoda sprawdzająca, czy gracz może użyć marchewki PVP
    public boolean canUsePvPCarrot(Player player) {
        long currentTime = System.currentTimeMillis(); // Pobranie bieżącego czasu w milisekundach
        if (!playerCooldowns.containsKey(player)) {
            return true; // Jeśli gracz nigdy nie używał marchewki, może ją teraz użyć
        }

        long lastUsed = playerCooldowns.get(player); // Czas ostatniego użycia marchewki przez gracza
        long cooldown = 60000; // Cooldown 1 minuty (60 000 milisekund)

        return currentTime - lastUsed >= cooldown; // Sprawdzamy, czy minęła minuta od ostatniego użycia
    }

    // Metoda do ustawienia cooldownu dla gracza
    public void setPvPCarrotCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        playerCooldowns.put(player, currentTime); // Zapisz czas ostatniego użycia marchewki
    }

    // Metoda, która informuje gracza o czasie pozostałym do ponownego użycia marchewki
    public void sendCooldownMessage(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = playerCooldowns.get(player);
        long cooldown = 60000; // Cooldown 1 minuty
        long timeRemaining = cooldown - (currentTime - lastUsed);
        long secondsRemaining = timeRemaining / 1000; // Czas pozostały w sekundach

        player.sendMessage("§8[§6§lX§f§l.PL§8] §cMożesz użyć marchewki PVP za " + secondsRemaining + " sekund.");
    }
}