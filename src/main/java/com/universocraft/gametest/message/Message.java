package com.universocraft.gametest.message;

import org.bukkit.command.CommandSender;

public enum Message {
    COMMAND_PLAYER_ONLY("command-player-only"),
    COMMAND_USAGE("command-usage"),

    ARENA_JOIN("arena-join"),
    ARENA_PLAYER_JOINED("arena-player-joined"),
    ARENA_LEAVE("arena-leave"),
    ARENA_PLAYER_LEFT("arena-player-left"),
    ARENA_WON("arena-won"),
    ARENA_LOST("arena-lost"),
    ARENA_NOT_FOUND("arena-not-found"),
    ARENA_FULL("arena-full"),
    ARENA_NOT_IN_ARENA("arena-not-in-arena"),
    ARENA_ALREADY_IN_ARENA("arena-already-in-arena"),
    ARENA_STARTED("arena-started"),

    SCOREBOARD_TITLE("scoreboard-title"),
    SCOREBOARD_WAITING("scoreboard-waiting"),
    SCOREBOARD_STARTING("scoreboard-starting"),
    SCOREBOARD_PLAYING("scoreboard-playing");

    private final String path;
    private String messageString;

    Message(String path) {
        this.path = path;
    }

    /**
     * Carga el mensaje desde un archivo de configuración.
     * Método interno.
     */
    void load() {
        messageString = MessageManager.getInstance().getMessage(path);
    }

    /**
     * Envía el mensaje a un jugador.
     * @param target jugador o consola
     */
    public void send(CommandSender target) {
        target.sendMessage(messageString);
    }

    /**
     * Envía el mensaje a una entidad, reemplazando un texto por otro.
     * @param target jugador o consola
     * @param replaced texto que será reemplazado
     * @param replacement texto de reemplazo
     */
    public void send(CommandSender target, CharSequence replaced, CharSequence replacement) {
        target.sendMessage(messageString.replace(replaced, replacement));
    }

    @Override
    public String toString() {
        return messageString;
    }
}
