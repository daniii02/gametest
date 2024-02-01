package com.universocraft.gametest;

import com.universocraft.earth.Earth;
import com.universocraft.earth.EarthListener;
import com.universocraft.gametest.arena.ArenaEventListener;
import com.universocraft.gametest.arena.ArenaManager;
import com.universocraft.gametest.command.JoinCommand;
import com.universocraft.gametest.command.LeaveCommand;
import com.universocraft.gametest.database.DatabaseManager;
import com.universocraft.gametest.listener.EarthListenerImpl;
import com.universocraft.gametest.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class GameTest extends JavaPlugin {
    private static GameTest instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        EarthListener listener = new EarthListenerImpl();

        // No tocar esto:
        Earth.getInstance().onEnable(this, listener, new DatabaseManager());

        Bukkit.getPluginManager().registerEvents(new ArenaEventListener(), this);

        MessageManager.getInstance().reloadMessages();
        ArenaManager.getInstance().reloadArenas();

        loadCommands();
    }

    private void loadCommands() {
        PluginCommand join = getCommand("join");
        join.setExecutor(new JoinCommand());
        join.setTabCompleter(new JoinCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    public static GameTest getInstance() {
        return instance;
    }
}