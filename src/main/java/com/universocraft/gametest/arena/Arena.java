package com.universocraft.gametest.arena;

import com.universocraft.gametest.message.Message;
import com.universocraft.gametest.player.ArenaPlayer;
import com.universocraft.gametest.player.ArenaPlayerManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Arena {
    private final String name, filePath;
    private final Location spawnA, spawnB;
    private Player playerA, playerB;
    private World world;

    // Por cada arena se crea un scoreboard
    private final Scoreboard scoreboard;
    private final Team team;

    private ArenaState state = ArenaState.WAITING;
    // Se usa tanto para el tiempo de espera como para el tiempo de partida según el estado
    private int secondsCounter;

    public Arena(String name, String filePath, Location spawnA, Location spawnB) {
        this.name = name;
        this.filePath = filePath;

        // Scoreboard
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective scoreboardObjective = scoreboard.registerNewObjective("arena-info", "dummy");
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboardObjective.setDisplayName(Message.SCOREBOARD_TITLE.toString().replace("{arena}", name));
        scoreboardObjective.getScore(ChatColor.WHITE.toString()).setScore(1);
        // Solo me hace falta una línea
        this.team = scoreboard.registerNewTeam("arena-info");
        this.team.addEntry(ChatColor.WHITE.toString());

        // Creo el mundo de la arena
        this.createWorld();

        // Hago que los spawns miren el uno al otro
        this.spawnA = spawnA.setDirection(spawnB.toVector().subtract(spawnA.toVector()).normalize());
        this.spawnB = spawnB.setDirection(spawnA.toVector().subtract(spawnB.toVector()).normalize());

        this.spawnA.setWorld(world);
        this.spawnB.setWorld(world);
    }

    private void unload() {
        world.getPlayers().forEach(this::restorePlayer);
        Bukkit.unloadWorld(world, false);
    }

    private void createWorld() {
        world = new WorldCreator(filePath).createWorld();
        world.setAutoSave(false);
    }

    /**
     * Reinicia la arena.
     */
    public void recreate() {
        // Reinicio el mundo
        unload();
        createWorld();
        // Reinicio los jugadores
        this.playerB = null;
        this.playerA = null;
        // Reinicio el estado
        this.state = ArenaState.WAITING;
    }

    /**
     * Cambia el estado para que se inicie el contador.
     */
    private void startMatch() {
        secondsCounter = 0;
        state = ArenaState.PLAYING;

        playerA.teleport(spawnA);
        playerB.teleport(spawnB);
        playerA.sendMessage(Message.ARENA_STARTED.toString());
        playerB.sendMessage(Message.ARENA_STARTED.toString());

        // Aquí iria el código para cargar los kits
        playerA.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        playerB.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
    }

    public void endMatch() {
        secondsCounter = ArenaManager.getInstance().getEndDelay();
        state = ArenaState.FINISHED;
    }

    /**
     * Actualiza la arena. Se ejecuta cada segundo.
     */
    void tick() {
        if (this.state == ArenaState.FINISHED) {
            if (secondsCounter-- > 0) return;
            this.recreate();
            return;
        }

        // No hay jugadores
        if (isEmpty()) return;

        // Hay dos jugadores
        if (isFull()) {
            if (state == ArenaState.STARTING) {
                if (secondsCounter-- > 0) {
                    setScoreboardLine(Message.SCOREBOARD_STARTING.toString().replace("{time}", getFormattedDuration()));
                    return;
                }

                this.startMatch();
            }
            secondsCounter++;
            setScoreboardLine(Message.SCOREBOARD_PLAYING.toString().replace("{time}", getFormattedDuration()));
            return;
        }

        // Hay un jugador
        // Actualizo en la socreboard "Esperando jugadores"
        setScoreboardLine(Message.SCOREBOARD_WAITING.toString());
    }

    /**
     * Actualiza una línea de la scoreboard.
     * @param line línea
     */
    private void setScoreboardLine(String line) {
        String[] split = line.split("#");
        team.setPrefix(split[0]);
        if (split.length > 1) team.setSuffix(split[1]);
    }

    /**
     * Añade un jugador a la arena.
     * @param player jugador a añadir
     * @return {@code true} si se ha añadido correctamente
     */
    public boolean addPlayer(Player player) {
        if (state == ArenaState.FINISHED) return false;

        if (needsPlayerA()) {
            playerA = player;
            player.teleport(spawnA);
            player.setGameMode(GameMode.SURVIVAL);
            if (playerB != null) Message.ARENA_PLAYER_JOINED.send(playerB, "{player}", player.getName());
        }
        else if (needsPlayerB()) {
            playerB = player;
            player.teleport(spawnB);
            player.setGameMode(GameMode.SURVIVAL);
            if (playerA != null) Message.ARENA_PLAYER_JOINED.send(playerA, "{player}", player.getName());
        }
        else return false;

        player.setScoreboard(scoreboard);
        // Si hay dos jugadores, empieza la cuenta atrás
        if (isFull()) {
            secondsCounter = ArenaManager.getInstance().getStartDelay();
            state = ArenaState.STARTING;
        }
        return true;
    }

    /**
     * Elimina un jugador de la arena.
     * @param player jugador a eliminar
     */
    public void removePlayer(Player player) {
        if (state == ArenaState.FINISHED) return;

        Player opponent = getOpponent(player);
        if (opponent != null) {
            if (state == ArenaState.PLAYING) {
                // Si están jugando, termina la partida
                onWin(opponent);
                addLoss(player);
                endMatch();
            }
            else {
                Message.ARENA_PLAYER_LEFT.send(opponent, "{player}", player.getName());
                state = ArenaState.WAITING;
            }
        }

        clearPlayer(player);
        restorePlayer(player);
    }

    /**
     * Restaura al jugador a su estado y ubicación original.
     * @param player jugador
     */
    public void restorePlayer(Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR) player.setGameMode(GameMode.SURVIVAL);
        ArenaManager.getInstance().teleportToSpawn(player);
        ArenaPlayer arenaPlayer = ArenaPlayerManager.getInstance().getArenaPlayer(player);
        if (arenaPlayer != null) arenaPlayer.setArena(null);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Se ejecuta cuando un jugador muere.
     * Termina la partida y actualiza las estadísticas.
     * @param player jugador que ha muerto
     */
    public void onPlayerDeath(Player player) {
        Player opponent = getOpponent(player);
        // No debería ser null nunca, pero por si acaso
        if (opponent != null) {
            Message.ARENA_LOST.send(player, "{player}", opponent.getName());
            onWin(opponent);
        }

        addLoss(player);

        player.setGameMode(GameMode.SPECTATOR);
        endMatch();
    }

    private Player getOpponent(Player player) {
        if (playerA == player) return playerB;
        if (playerB == player) return playerA;
        return null;
    }

    private void clearPlayer(Player player) {
        if (playerA == player) playerA = null;
        else if (playerB == player) playerB = null;
    }

    /**
     * Añade una victoria al jugador, y crea un efecto.
     * @param player jugador
     */
    private void onWin(Player player) {
        ArenaPlayerManager.getInstance().getArenaPlayer(player).getDatabaseEntry().addWin();
        Message.ARENA_WON.send(player);

        // Fuego artificial en la posición del jugador
        world.spawnEntity(player.getLocation(), EntityType.FIREWORK);
    }

    /**
     * Añade una derrota al jugador.
     * @param player jugador
     */
    private void addLoss(Player player) {
        ArenaPlayerManager.getInstance().getArenaPlayer(player).getDatabaseEntry().addLoss();
    }

    public boolean needsPlayerA() {
        return playerA == null;
    }

    public boolean needsPlayerB() {
        return playerB == null;
    }

    /**
     * Comprueba si la arena está llena.
     * @return {@code true} si está llena
     */
    public boolean isFull() {
        return !needsPlayerA() && !needsPlayerB();
    }

    /**
     * Comprueba si la arena está vacía.
     * @return {@code true} si está vacía
     */
    public boolean isEmpty() {
        return needsPlayerA() && needsPlayerB();
    }

    private String getFormattedDuration() {
        return DurationFormatUtils.formatDuration(secondsCounter * 1000L, "mm:ss");
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public ArenaState getState() {
        return state;
    }
}
