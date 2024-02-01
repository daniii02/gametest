package com.universocraft.gametest.arena;

public enum ArenaState {
    WAITING, // La arena está esperando a que se llene
    STARTING, // La partida está empezando
    PLAYING, // La partida está en curso
    FINISHED // La partida ha terminado y teletransportará a los jugadores al lobby
}
