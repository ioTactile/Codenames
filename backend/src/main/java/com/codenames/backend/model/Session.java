package com.codenames.backend.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "players", joinColumns = @JoinColumn(name = "id"))
    @Embedded
    private List<Player> players;

    @ElementCollection
    @CollectionTable(name = "words", joinColumns = @JoinColumn(name = "id"))
    @Embedded
    private List<Word> words;

    @ElementCollection
    @CollectionTable(name = "clues", joinColumns = @JoinColumn(name = "id"))
    @Embedded
    private List<Clue> clues;

    @Column(name = "team_turn", nullable = false)
    private String teamTurn;
    @Column(name = "status", nullable = false)
    private SessionStatus status;
    @Column(name = "red_remaining_words", nullable = false)
    private int redRemainingWords;
    @Column(name = "blue_remaining_words", nullable = false)
    private int blueRemainingWords;
    @Column(name = "is_black_card", nullable = false)
    private Boolean isBlackCard;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Session() {
    }

    public Session(
            Long id,
            List<Player> players,
            List<Word> words,
            List<Clue> clues,
            String teamTurn,
            SessionStatus status,
            int redRemainingWords,
            int blueRemainingWords,
            Boolean isBlackCard,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
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

    public Session(
            List<Player> players,
            List<Word> words,
            List<Clue> clues,
            String teamTurn,
            SessionStatus status,
            int redRemainingWords,
            int blueRemainingWords,
            Boolean isBlackCard,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Clue> getClues() {
        return clues;
    }

    public void setClues(List<Clue> clues) {
        this.clues = clues;
    }

    public String getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(String teamTurn) {
        this.teamTurn = teamTurn;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public int getRedRemainingWords() {
        return redRemainingWords;
    }

    public void setRedRemainingWords(int redRemainingWords) {
        this.redRemainingWords = redRemainingWords;
    }

    public int getBlueRemainingWords() {
        return blueRemainingWords;
    }

    public void setBlueRemainingWords(int blueRemainingWords) {
        this.blueRemainingWords = blueRemainingWords;
    }

    public Boolean getIsBlackCard() {
        return isBlackCard;
    }

    public void setIsBlackCard(Boolean isBlackCard) {
        this.isBlackCard = isBlackCard;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
