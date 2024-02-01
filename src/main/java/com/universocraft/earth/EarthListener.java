package com.universocraft.earth;

import com.universocraft.earth.core.CorePlayer;

public interface EarthListener {

    // Evento cuando entró realmente el jugador.
    default void onPlayerJoinEvent(CorePlayer corePlayer) {}

    // Evento cuando ya se ha guardado la base de datos al salir el jugador.
    default void onPlayerSecureRemoveEvent(CorePlayer corePlayer) {}

}
