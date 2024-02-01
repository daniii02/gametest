package com.universocraft.gametest.player;

import com.mongodb.lang.Nullable;
import com.universocraft.gametest.arena.Arena;
import com.universocraft.gametest.database.DatabaseEntry;

public class ArenaPlayer {
    private final DatabaseEntry databaseEntry;
    // Representa la arena en la que está el jugador
    private @Nullable Arena arena;

    public ArenaPlayer(DatabaseEntry databaseEntry) {
        this.databaseEntry = databaseEntry;
    }

    public DatabaseEntry getDatabaseEntry() {
        return databaseEntry;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }
}
