package pl.revson.xPLAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class XPLAPI extends JavaPlugin implements Listener {

    private final String goldenCarrotName = "§6Marchewka PVP";
    private final Material goldenCarrotMaterial = Material.GOLDEN_CARROT;
    private final String swordName = "§6Miecz PVP"; // Nazwa miecza PVP
    private final PVPColdown pvpCooldown = new PVPColdown(); // Instancja klasy PVPColdown


    @Override
    public void onEnable() {
        // Rejestracja eventów
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        // Sprawdzenie, czy gracz przesiada się na złotą marchewkę PVP
        if (isPvPCarrot(newItem)) {
            // Sprawdzenie, czy gracz może użyć marchewki (czy minęła minuta)
            if (pvpCooldown.canUsePvPCarrot(player)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        givePvPSet(player); // Dodanie zestawu PVP
                        player.getInventory().setItem(event.getNewSlot(), createSword()); // Zamiana na miecz
                        player.sendMessage("§8[§6§lX§f§l.PL§8] §aAktywowałeś zestaw PVP!");

                        // Po 2 sekundach dezaktywujemy zestaw
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                removePvPSet(player); // Usunięcie zestawu PVP
                                replaceSwordWithCarrot(player); // Zamiana miecza na marchewkę
                                player.sendMessage("§8[§6§lX§f§l.PL§8] §cZestaw PVP został dezaktywowany.");
                            }
                        }.runTaskLater(XPLAPI.this, 40L); // 2 sekundy opóźnienia (40 ticków = 2 sekundy)
                    }
                }.runTaskLater(this, 40L); // 2 sekundy opóźnienia (20 ticków = 1 sekunda)

                // Ustawienie cooldownu na 1 minutę
                pvpCooldown.setPvPCarrotCooldown(player);
            } else {
                // Jeśli cooldown nie minął, gracz dostaje informację
                pvpCooldown.sendCooldownMessage(player);
            }
        }
        // Jeśli gracz przesiada się na miecz PVP, nic nie robimy
        else if (isPvPSword(newItem)) {
            // Nie robimy nic
        }
        // W przypadku zmiany na coś innego
        else {
            if (hasPvPSet(player) && isPvPSword(oldItem)) {
                // Usunięcie zestawu PVP i zamiana miecza na złotą marchewkę
                removePvPSet(player);
                replaceSwordWithCarrot(player); // Zamiana miecza na złotą marchewkę
                player.sendMessage("§8[§6§lX§f§l.PL§8] §cZestaw PVP został dezaktywowany.");
            }
        }
    }

    private void givePvPSet(Player player) {
        // Przyznanie zestawu PVP (widocznie diamentowa zbroja)
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    }

    private void removePvPSet(Player player) {
        // Usunięcie zbroi
        if (hasPvPSet(player)) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }
    }

    private ItemStack createSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        if (swordMeta != null) {
            swordMeta.setDisplayName(swordName);
            sword.setItemMeta(swordMeta);
        }
        return sword;
    }

    private ItemStack createPvPCarrot() {
        ItemStack goldenCarrot = new ItemStack(goldenCarrotMaterial);
        ItemMeta meta = goldenCarrot.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(goldenCarrotName);
            goldenCarrot.setItemMeta(meta);
        }
        return goldenCarrot;
    }

    private void replaceSwordWithCarrot(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isPvPSword(item)) {
                ItemStack carrot = createPvPCarrot();
                player.getInventory().setItem(i, carrot); // Zamiana miecza na marchewkę
                break; // Kończymy pętlę po zamianie
            }
        }
    }

    private boolean isPvPSword(ItemStack item) {
        return item != null && item.getType() == Material.DIAMOND_SWORD &&
                item.getItemMeta() != null && swordName.equals(item.getItemMeta().getDisplayName());
    }

    private boolean isPvPCarrot(ItemStack item) {
        return item != null && item.getType() == goldenCarrotMaterial &&
                item.getItemMeta() != null && goldenCarrotName.equals(item.getItemMeta().getDisplayName());
    }

    private boolean hasPvPSet(Player player) {
        return player.getInventory().getHelmet() != null &&
                player.getInventory().getChestplate() != null &&
                player.getInventory().getLeggings() != null &&
                player.getInventory().getBoots() != null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Dodanie złotej marchewki do ekwipunku gracza
        giveGoldenCarrot(player);
    }

    private void giveGoldenCarrot(Player player) {
        ItemStack goldenCarrot = createPvPCarrot(); // Stworzenie marchewki

        // Dodanie marchewki do ekwipunku gracza
        if (!hasGoldenCarrot(player)) {
            player.getInventory().setItem(6, goldenCarrot); // Dodaj złotą marchewkę na 7. slot (indeks 6)
        }
    }

    private boolean hasGoldenCarrot(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && isPvPCarrot(item) && item.getAmount() == 1) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        // Anuluj wyrzucanie złotej marchewki PVP
        if (isPvPCarrot(item)) {
            event.setCancelled(true);
        }
    }
}