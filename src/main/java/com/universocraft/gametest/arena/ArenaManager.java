package com.universocraft.gametest.arena;

import com.mongodb.lang.Nullable;
import com.universocraft.gametest.GameTest;
import com.universocraft.gametest.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {
    private static ArenaManager instance;

    private final Map<String, Arena> arenasByName = new HashMap<>();

    // Mundo al que se teletransportan los jugadores al salir de una arena
    private final WeakReference<World> spawnWorld;
    // Tiempo de espera para empezar una partida
    private int startDelay, endDelay;

    public ArenaManager() {
        Bukkit.getScheduler().runTaskTimer(GameTest.getInstance(), this::tickArenas, 20, 20);

        FileConfiguration config = GameTest.getInstance().getConfig();

        String spawnWorld = config.getString("spawn-world");
        this.spawnWorld = new WeakReference<>(Bukkit.getWorld(spawnWorld));

        this.startDelay = config.getInt("start-delay");
        if (this.startDelay <= 0) {
            GameTest.getInstance().warn("El tiempo de espera para empezar una partida debe ser mayor que 0.");
            this.startDelay = 10;
        }

        this.endDelay = config.getInt("end-delay");
        if (this.endDelay <= 0) {
            GameTest.getInstance().warn("El tiempo de espera al terminar una partida debe ser mayor que 0.");
            this.endDelay = 50;
        }
    }

    /**
     * Recarga las arenas de la configuración.
     */
    public void reloadArenas() {
        this.arenasByName.clear();

        File arenas = new File(GameTest.getInstance().getDataFolder(), "arenas");
        if (!arenas.exists()) arenas.mkdir();
        if (!arenas.isDirectory()) {
            GameTest.getInstance().warn("El archivo arenas debe ser un directorio.");
            return;
        }

        ConfigurationSection section = GameTest.getInstance().getConfig().getConfigurationSection("arenas");
        for (String arenaName : section.getKeys(false)) {
            ConfigurationSection arenaSection = section.getConfigurationSection(arenaName);

            loadArena(arenaName.toLowerCase(), arenaSection, arenas);
        }

        int size = this.arenasByName.size();
        switch (size) {
            case 0:
                GameTest.getInstance().info("No se ha cargado ninguna arena.");
                break;
            case 1:
                GameTest.getInstance().info("Se ha cargado una arena.");
                break;
            default:
                GameTest.getInstance().info("Se han cargado " + size + " arenas.");
                break;
        }
    }

    /**
     * Carga una arena a partir de una sección de configuración.
     * @param arenaName nombre de la arena
     * @param arenaSection sección de configuración
     * @param arenasFolder directorio de las arenas
     */
    private void loadArena(String arenaName, ConfigurationSection arenaSection, File arenasFolder) {
        if (this.arenasByName.containsKey(arenaName)) {
            GameTest.getInstance().warn("La arena " + arenaName + " ya está cargada.");
            return;
        }

        try {
            String file = arenaSection.getString("file");
            File arenaFile = new File(arenasFolder, file);
            if (!arenaFile.exists()) {
                GameTest.getInstance().warn("No se ha encontrado el archivo " + file + ".");
                return;
            }

            if (!arenaFile.isDirectory()) {
                GameTest.getInstance().warn("El archivo " + file + " debe ser un directorio.");
                return;
            }

            List<Float> spawnAFloats = arenaSection.getFloatList("spawnA");
            if (spawnAFloats.size() != 3) {
                GameTest.getInstance().warn("El spawn A de la arena " + arenaName + " no es válido.");
                return;
            }

            Location spawnA = ConfigManager.getInstance().parseLocation(arenaSection, "spawnA");
            Location spawnB = ConfigManager.getInstance().parseLocation(arenaSection, "spawnB");

            this.arenasByName.put(arenaName, new Arena(arenaName, arenaFile.getPath(), spawnA, spawnB));
        }
        catch (Exception e) {
            GameTest.getInstance().warn("Error al cargar la arena " + arenaName + ".");
            e.printStackTrace();
        }
    }

    /**
     * Metodo interno para actualizar las arenas.
     */
    private void tickArenas() {
        this.arenasByName.values().forEach(Arena::tick);
    }

    /**
     * Teletransporta a un jugador al spawn.
     * Si el mundo ha sido borrado, se teletransporta al primer mundo.
     * @param player jugador a teletransportar
     */
    public void teleportToSpawn(Player player) {
        World world = spawnWorld.get();
        if (world == null) world = Bukkit.getWorlds().get(0);
        player.teleport(world.getSpawnLocation());
    }

    public int getStartDelay() {
        return startDelay;
    }

    public int getEndDelay() {
        return endDelay;
    }

    /**
     * Devuelve una arena a partir de su nombre.
     * @param name nombre de la arena
     * @return arena o {@code null} si no existe
     */
    public @Nullable Arena getArena(String name) {
        return this.arenasByName.get(name.toLowerCase());
    }

    public List<String> getArenaNames() {
        return new ArrayList<>(this.arenasByName.keySet());
    }

    public static synchronized ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }
}
