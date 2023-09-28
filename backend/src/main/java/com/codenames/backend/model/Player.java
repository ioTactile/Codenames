package com.codenames.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Player {

    private String name;
    private PlayerTeam playerTeam;
    private PlayerRole playerRole;

    public Player() {

    }

    public Player(String name, PlayerTeam playerTeam, PlayerRole playerRole) {
        this.name = name;
        this.playerTeam = playerTeam;
        this.playerRole = playerRole;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public PlayerTeam getPlayerTeam() {
        return playerTeam;
    }
    public void setPlayerTeam(PlayerTeam playerTeam) {
        this.playerTeam = playerTeam;
    }

    public PlayerRole getPlayerRole() {
        return playerRole;
    }
    public void setPlayerRole(PlayerRole playerRole) {
        this.playerRole = playerRole;
    }
}
