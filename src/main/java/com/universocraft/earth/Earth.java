package com.universocraft.earth;

import com.universocraft.earth.core.CorePlayer;
import com.universocraft.earth.database.CraftDatabaseManager;
import com.universocraft.gametest.database.DatabaseEntry;
import com.universocraft.gametest.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Earth implements Listener {

    private static Earth instance;

    private EarthListener coreListener;
    private CraftDatabaseManager databaseManager;

    public void onEnable(JavaPlugin plugin, EarthListener coreListener, DatabaseManager databaseManager) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.coreListener = coreListener;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = new CorePlayer(player);
        DatabaseEntry entry = new DatabaseEntry();
        entry.load(this.databaseManager.findById(player.getUniqueId().toString()));
        corePlayer.setDatabaseEntry(entry);
        this.coreListener.onPlayerJoinEvent(corePlayer);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = new CorePlayer(player);
        this.databaseManager.save(player.getUniqueId().toString(), this.databaseManager.onSave(corePlayer));
        this.coreListener.onPlayerSecureRemoveEvent(corePlayer);
    }

    public static void returnLobby(Player player) {
        player.kickPlayer("¡Regresando al lobby!");
    }

    public static synchronized Earth getInstance() {
        if (instance == null) {
            instance = new Earth();
        }
        return instance;
    }


}
