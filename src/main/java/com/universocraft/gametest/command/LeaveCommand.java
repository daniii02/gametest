package com.universocraft.gametest.command;

import com.universocraft.gametest.arena.Arena;
import com.universocraft.gametest.message.Message;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String currentAlias, String[] args) {
        if (!(sender instanceof Player)) {
            Message.COMMAND_PLAYER_ONLY.send(sender);
            return true;
        }

        Player player = (Player) sender;

        Arena arena = ArenaPlayerManager.getInstance().getArenaPlayer(player).getArena();
        if (arena == null) {
            Message.ARENA_NOT_IN_ARENA.send(player);
            return true;
        }

        arena.removePlayer(player);
        Message.ARENA_LEAVE.send(player, "{arena}", arena.getName());
        return true;
    }
}
