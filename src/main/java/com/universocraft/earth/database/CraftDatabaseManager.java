package com.universocraft.earth.database;

import com.universocraft.earth.core.CorePlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public abstract class CraftDatabaseManager {

    private final Map<String, Document> mapping = new HashMap<>();

    public abstract Document onSave(CorePlayer corePlayer);

    public Document findById(String uuid) {
        return this.mapping.getOrDefault(uuid, new Document());
    }

    public void save(String uuid, Document document) {
        this.mapping.put(uuid, document);
    }

}
