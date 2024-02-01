package com.universocraft.earth.core;

import com.universocraft.gametest.database.DatabaseEntry;
import org.bukkit.entity.Player;

public class CorePlayer {

    private final Player bukkit;

    private DatabaseEntry databaseEntry;

    public CorePlayer(Player bukkit) {
        this.bukkit = bukkit;
    }

    public Player getBukkit() {
        return bukkit;
    }

    public DatabaseEntry getDatabaseEntry() {
        return databaseEntry;
    }

    public void setDatabaseEntry(DatabaseEntry databaseEntry) {
        this.databaseEntry = databaseEntry;
    }

}
