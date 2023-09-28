package com.codenames.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Clue {
    
    private String name;
    private int attempts;
    private int remaining;
    private String team;

    public Clue() {

    }

    public Clue(String name, int attempts, int remaining, String team) {
        this.name = name;
        this.attempts = attempts;
        this.remaining = remaining;
        this.team = team;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getRemaining() {
        return remaining;
    }
    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String getTeam() {
        return team;
    }
    public void setTeam(String team) {
        this.team = team;
    }
}
