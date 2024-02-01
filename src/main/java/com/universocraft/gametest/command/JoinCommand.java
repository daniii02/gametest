package com.universocraft.gametest.command;

import com.universocraft.gametest.arena.Arena;
import com.universocraft.gametest.arena.ArenaManager;
import com.universocraft.gametest.message.Message;
import com.universocraft.gametest.player.ArenaPlayer;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String currentAlias, String[] args) {
        if (!(sender instanceof Player)) {
            Message.COMMAND_PLAYER_ONLY.send(sender);
            return true;
        }

        Player player = (Player) sender;
        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().getArenaPlayer(player);
        if (arenaPlayer.getArena() != null) {
            Message.ARENA_ALREADY_IN_ARENA.send(player, "{arena}", arenaPlayer.getArena().getName());
            return true;
        }

        if (args.length == 0) {
            Message.COMMAND_USAGE.send(sender, "{command}", "/join <arena>");
            return true;
        }

        String arenaName = args[0];
        Arena arena = ArenaManager.getInstance().getArena(arenaName);

        if (arena == null) {
            Message.ARENA_NOT_FOUND.send(player, "{arena}", arenaName);
            return true;
        }

        if (arena.addPlayer(player)) {
            Message.ARENA_JOIN.send(player, "{arena}", arenaName);
            arenaPlayer.setArena(arena);
        }
        else {
            Message.ARENA_FULL.send(player, "{arena}", arenaName);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String currentAlias, String[] args) {
        return args.length == 1 ? ArenaManager.getInstance().getArenaNames() : null;
    }
}
