package com.codenames.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Clue {

    private String clueName;
    private int attempts;
    private int remaining;
    private String spyName;

    public Clue() {

    }

    public Clue(String clueName, int attempts, int remaining, String spyName) {
        this.clueName = clueName;
        this.attempts = attempts;
        this.remaining = remaining;
        this.spyName = spyName;
    }

    public String getClueName() {
        return clueName;
    }

    public void setClueName(String clueName) {
        this.clueName = clueName;
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

    public String getSpyName() {
        return spyName;
    }

    public void setSpyName(String spyName) {
        this.spyName = spyName;
    }
}
