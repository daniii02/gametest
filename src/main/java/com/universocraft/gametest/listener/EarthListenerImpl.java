package com.universocraft.gametest.listener;

import com.universocraft.earth.EarthListener;
import com.universocraft.earth.core.CorePlayer;
import com.universocraft.gametest.player.ArenaPlayer;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.bukkit.entity.Player;

public class EarthListenerImpl implements EarthListener {
    @Override
    public void onPlayerJoinEvent(CorePlayer corePlayer) {
        ArenaPlayerManager.getInstance().setArenaPlayer(corePlayer);
    }

    @Override
    public void onPlayerSecureRemoveEvent(CorePlayer corePlayer) {
        Player player = corePlayer.getBukkit();

        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().removeArenaPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena() == null) {
            return;
        }

        arenaPlayer.getArena().removePlayer(player);
    }
}
