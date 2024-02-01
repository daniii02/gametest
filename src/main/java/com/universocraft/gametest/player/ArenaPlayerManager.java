package com.universocraft.gametest.player;

import com.universocraft.earth.core.CorePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ArenaPlayerManager {
    private static ArenaPlayerManager instance;

    private final Map<Player, ArenaPlayer> arenaPlayers = new HashMap<>();

    public ArenaPlayer getArenaPlayer(Player player) {
        return arenaPlayers.get(player);
    }

    public void setArenaPlayer(CorePlayer corePlayer) {
        arenaPlayers.put(corePlayer.getBukkit(), new ArenaPlayer(corePlayer.getDatabaseEntry()));
    }

    public ArenaPlayer removeArenaPlayer(Player player) {
        return arenaPlayers.remove(player);
    }

    /**
     * Devuelve si el jugador está en una arena.
     * @param player jugador
     * @return {@code true} si está en una arena
     */
    public boolean isInArena(Player player) {
        return arenaPlayers.get(player).getArena() != null;
    }

    public static synchronized ArenaPlayerManager getInstance() {
        if (instance == null) {
            instance = new ArenaPlayerManager();
        }
        return instance;
    }
}
