package com.universocraft.gametest.manager;

import com.universocraft.gametest.GameTest;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ConfigManager {
    private static ConfigManager instance;

    /**
     * Carga una configuración a partir del nombre introducido.
     * @param name nombre del archivo de configuración
     * @return configuración cargada
     * @throws RuntimeException si ocurre un error al cargar la configuración
     */
    public YamlConfiguration loadConfiguration(String name) throws RuntimeException {
        File file = new File(GameTest.getInstance().getDataFolder(), name);
        if (!file.exists()) {
            GameTest.getInstance().saveResource(name, false);
        }

        // Creo y cargo la configuración
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Error al cargar la configuración " + name + ".", e);
        }
        return config;
    }

    public Location parseLocation(ConfigurationSection section, String path) {
        List<Float> floatList = section.getFloatList(path);
        if (floatList.size() != 3) {
            throw new RuntimeException("La location " + path + " no es válida.");
        }
        return new Location(null, floatList.get(0), floatList.get(1), floatList.get(2));
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}