package com.universocraft.gametest.arena;

import com.universocraft.gametest.player.ArenaPlayer;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ArenaEventListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().getArenaPlayer(player);

        Arena arena = arenaPlayer.getArena();
        if (arena == null || arena.getState() == ArenaState.PLAYING) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().getArenaPlayer(player);

        Arena arena = arenaPlayer.getArena();
        if (arena == null || arena.getState() != ArenaState.PLAYING) return;

        arena.onPlayerDeath(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(ArenaPlayerManager.getInstance().isInArena(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(ArenaPlayerManager.getInstance().isInArena(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().getArenaPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (arena == null) return;

        // Si el mundo de la arena es el mismo del que se ha cambiado el jugador
        if (arena.getWorld() == event.getFrom()) arena.removePlayer(player);
    }
}
