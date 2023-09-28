package com.codenames.backend.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    private Long sessionId;
    private List<Player> players;
    private List<Word> words;
    private List<Clue> clues;
    private String teamTurn;
    private SessionStatus status;
    private int redRemainingWords;
    private int blueRemainingWords;
    private Boolean isBlackCard;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Session() {
    }

    public Session(
            Long sessionId,
            List<Player> players,
            List<Word> words,
            List<Clue> clues,
            String teamTurn,
            SessionStatus status,
            int redRemainingWords,
            int blueRemainingWords,
            Boolean isBlackCard,
            LocalDate createdAt,
            LocalDate updatedAt) {
        this.sessionId = sessionId;
        this.players = players;
        this.words = words;
        this.clues = clues;
        this.teamTurn = teamTurn;
        this.status = status;
        this.redRemainingWords = redRemainingWords;
        this.blueRemainingWords = blueRemainingWords;
        this.isBlackCard = isBlackCard;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    public Long getSessionId() {
        return sessionId;
    }
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @ElementCollection
    @CollectionTable(name = "players", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "players", nullable = false)
    public List<Player> getPlayers() {
        return players;
    }
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @ElementCollection
    @CollectionTable(name = "words", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "words", nullable = false)
    public List<Word> getWords() {
        return words;
    }
    public void setWords(List<Word> words) {
        this.words = words;
    }

    @ElementCollection
    @CollectionTable(name = "clues", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "clues", nullable = false)
    public List<Clue> getClues() {
        return clues;
    }
    public void setClues(List<Clue> clues) {
        this.clues = clues;
    }

    @Column(name = "team_turn", nullable = false)
    public String getTeamTurn() {
        return teamTurn;
    }
    public void setTeamTurn(String teamTurn) {
        this.teamTurn = teamTurn;
    }

    @Column(name = "status", nullable = false)
    public SessionStatus getStatus() {
        return status;
    }
    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    @Column(name = "red_remaining_words", nullable = false)
    public int getRedRemainingWords() {
        return redRemainingWords;
    }
    public void setRedRemainingWords(int redRemainingWords) {
        this.redRemainingWords = redRemainingWords;
    }

    @Column(name = "blue_remaining_words", nullable = false)
    public int getBlueRemainingWords() {
        return blueRemainingWords;
    }
    public void setBlueRemainingWords(int blueRemainingWords) {
        this.blueRemainingWords = blueRemainingWords;
    }

    @Column(name = "is_black_card", nullable = false)
    public Boolean getIsBlackCard() {
        return isBlackCard;
    }
    public void setIsBlackCard(Boolean isBlackCard) {
        this.isBlackCard = isBlackCard;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "updated_at", nullable = false)
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
