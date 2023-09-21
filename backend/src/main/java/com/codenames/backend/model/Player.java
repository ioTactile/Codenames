package com.codenames.backend.model;

public class Player {
    
    private String name;
    private PlayerTeam playerTeam;

    public Player(String name, PlayerTeam playerTeam) {
        this.name = name;
        this.playerTeam = playerTeam;
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
}
