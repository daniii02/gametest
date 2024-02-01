package com.universocraft.gametest.database;

import com.universocraft.earth.core.CorePlayer;
import com.universocraft.earth.database.CraftDatabaseManager;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Date;

public class DatabaseManager extends CraftDatabaseManager {
    @Override
    public Document onSave(CorePlayer corePlayer) {
        Document document = new Document();

        Player player = corePlayer.getBukkit();

        DatabaseEntry databaseEntry = ArenaPlayerManager.getInstance().getArenaPlayer(player).getDatabaseEntry();

        document.put("_id", player.getUniqueId());
        document.put("coins", databaseEntry.getCoins());
        document.put("wins", databaseEntry.getWins());
        document.put("loses", databaseEntry.getLoses());
        document.put("last_played", new Date());

        return document;
    }
}
