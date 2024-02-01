package com.universocraft.gametest.database;

import org.bson.Document;

import java.util.Date;

public class DatabaseEntry {
    private int coins, wins, loses;
    private Date lastPlayed;

    public void load(Document document) {
        this.coins = document.getInteger("coins", 0);
        this.wins = document.getInteger("wins", 0);
        this.loses = document.getInteger("loses", 0);
        this.lastPlayed = document.getDate("last_played");
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getLoses() {
        return loses;
    }

    public void addLoss() {
        this.loses++;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
