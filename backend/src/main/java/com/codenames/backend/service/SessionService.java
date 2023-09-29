package com.codenames.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codenames.backend.model.Clue;
import com.codenames.backend.model.Player;
import com.codenames.backend.model.PlayerRole;
import com.codenames.backend.model.PlayerTeam;
import com.codenames.backend.model.Session;
import com.codenames.backend.model.SessionStatus;
import com.codenames.backend.model.Word;
import com.codenames.backend.model.WordColor;
import com.codenames.backend.model.WordState;
import com.codenames.backend.repository.SessionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(String pseudo) {
        Session session = new Session();

        session.setPlayers(new ArrayList<>());
        session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE));

        List<Word> words = new ArrayList<>();

        try {
            words = getWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.setWords(words);
        session.setClues(new ArrayList<>());

        int redCount = (int) words.stream().filter(word -> word.getWordColor() == WordColor.RED).count();
        int blueCount = (int) words.stream().filter(word -> word.getWordColor() == WordColor.BLUE).count();

        if (redCount > blueCount) {
            session.setTeamTurn(PlayerTeam.RED.toString());
        } else {
            session.setTeamTurn(PlayerTeam.BLUE.toString());
        }

        session.setRedRemainingWords(redCount);
        session.setBlueRemainingWords(blueCount);
        session.setIsBlackCard(false);
        session.setStatus(SessionStatus.PENDING);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        sessionRepository.save(session);

        return session;
    }

    public Session getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    public void deleteSessionById(Long sessionId) {
        boolean exists = sessionRepository.existsById(sessionId);
        if (!exists) {
            return;
        }
        sessionRepository.deleteById(sessionId);
    }

    public void joinSession(Long sessionId, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.PENDING)) {
            return;
        }
        session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE));
        sessionRepository.save(session);
    }

    public void joinSessionAsSpectator(Long sessionId) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
            return;
        }
        String pseudo = "Spectateur " + (int) session.getPlayers().stream()
                .filter(player -> player.getPlayerRole().equals(PlayerRole.SPECTATOR)).count();
        session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.SPECTATOR));
        sessionRepository.save(session);
    }

    public void leaveSession(Long sessionId, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.PENDING)) {
            return;
        }
        session.getPlayers().removeIf(player -> player.getName().equals(pseudo));
        sessionRepository.save(session);
    }

    public void startSession(Long sessionId) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.PENDING)) {
            return;
        }
        if (session.getPlayers().size() < 4) {
            return;
        }
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public void selectTeam(Long sessionId, String team, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.PENDING)) {
            return;
        }
        Player player = session.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            return;
        }
        player.setPlayerTeam(mapStringToPlayerTeam(team));
        sessionRepository.save(session);
    }

    public void selectRole(Long sessionId, String role, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.PENDING)) {
            return;
        }
        Player player = session.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            throw new IllegalStateException("Player with name " + pseudo + " does not exist");
        }
        if (role.equals(PlayerRole.SPYMASTER.toString())) {
            for (Player p : session.getPlayers()) {
                if (p.getPlayerTeam().equals(player.getPlayerTeam())
                        && p.getPlayerRole().equals(PlayerRole.SPYMASTER)) {
                    p.setPlayerRole(PlayerRole.OPERATIVE);
                }
            }
        }
        player.setPlayerRole(mapStringToPlayerRole(role));
        sessionRepository.save(session);
    }

    public void shufflePlayers(Long sessionId) {
        Session session = getSessionById(sessionId);
        if (session == null) {
            throw new IllegalStateException("Session with id " + sessionId + " does not exist");
        }
        List<Player> players = session.getPlayers();
        Collections.shuffle(players);
        List<Player> redPlayers = new ArrayList<>();
        List<Player> bluePlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                redPlayers.add(players.get(i));
            } else {
                bluePlayers.add(players.get(i));
            }
        }
        for (Player player : redPlayers) {
            player.setPlayerTeam(PlayerTeam.RED);
            player.setPlayerRole(PlayerRole.NONE);
        }
        for (Player player : bluePlayers) {
            player.setPlayerTeam(PlayerTeam.BLUE);
            player.setPlayerRole(PlayerRole.NONE);
        }
        session.getPlayers().clear();
        session.getPlayers().addAll(bluePlayers);
        session.getPlayers().addAll(redPlayers);
        sessionRepository.save(session);
    }

    public void switchTeamTurn(Long sessionId) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
            return;
        }
        if (session.getTeamTurn().equals(PlayerTeam.RED.toString())) {
            session.setTeamTurn(PlayerTeam.BLUE.toString());
        } else {
            session.setTeamTurn(PlayerTeam.RED.toString());
        }
        sessionRepository.save(session);
    }

    public void selectWord(Long sessionId, String wordName, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
            return;
        }
        Player player = session.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            return;
        }
        Word word = session.getWords().stream()
                .filter(w -> w.getWordName().equals(wordName)).findFirst().orElse(null);
        if (word == null) {
            return;
        }
        if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
            if (word.getWordState().equals(WordState.SELECTED)) {
                word.setWordState(WordState.NOT_SELECTED);
                word.getSelectedBy().remove(player.getName());
            }
            word.setWordState(WordState.SELECTED);
            word.getSelectedBy().add(player.getName());
            sessionRepository.save(session);
        }
    }

    public void clickWord(Long sessionId, String wordName, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
            return;
        }
        Player player = session.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            return;
        }
        Word word = session.getWords().stream()
                .filter(w -> w.getWordName().equals(wordName)).findFirst().orElse(null);
        if (word == null) {
            return;
        }
        if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
            if (session.getClues().size() == 0) {
                return;
            }
            word.setWordState(WordState.CLICKED);
            if (word.getWordColor() == WordColor.RED) {
                session.setRedRemainingWords(session.getRedRemainingWords() - 1);
            } else if (word.getWordColor() == WordColor.BLUE) {
                session.setBlueRemainingWords(session.getBlueRemainingWords() - 1);
            } else if (word.getWordColor() == WordColor.WHITE) {
                switchTeamTurn(sessionId);
            } else if (word.getWordColor() == WordColor.BLACK) {
                session.setIsBlackCard(true);
            }
            if (session.getRedRemainingWords() == 0) {
                session.setStatus(SessionStatus.RED_TEAM_WINS);
                session.setUpdatedAt(LocalDateTime.now());
            } else if (session.getBlueRemainingWords() == 0) {
                session.setStatus(SessionStatus.BLUE_TEAM_WINS);
                session.setUpdatedAt(LocalDateTime.now());
            } else if (session.getIsBlackCard()) {
                if (session.getTeamTurn().equals(PlayerTeam.RED.toString())) {
                    session.setStatus(SessionStatus.BLUE_TEAM_WINS);
                    session.setUpdatedAt(LocalDateTime.now());
                } else {
                    session.setStatus(SessionStatus.RED_TEAM_WINS);
                    session.setUpdatedAt(LocalDateTime.now());
                }
            }
            if (session.getClues().size() > 0) {
                Clue clue = session.getClues().get(session.getClues().size() - 1);
                clue.setRemaining(clue.getRemaining() - 1);
                if (clue.getRemaining() == 0) {
                    switchTeamTurn(sessionId);
                }
            }
            sessionRepository.save(session);
        }
    }

    public void addClue(Long sessionId, Clue clue, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session == null || !session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
            return;
        }
        Player player = session.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            return;
        }
        if (!player.getPlayerRole().equals(PlayerRole.SPYMASTER) || !player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
            return;
        }
        if (session.getWords().stream().anyMatch(word -> word.getWordName().equals(clue.getClueName()))) {
            return;
        }
        int remaining = clue.getAttempts() + 1;
        session.getClues().add(new Clue(clue.getClueName(), clue.getAttempts(), remaining, player.getName()));
        sessionRepository.save(session);
    }

    private List<Word> getWords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(getClass().getResourceAsStream("/words.json"));
        JsonNode frenchWordsNode = rootNode.path("French");
        List<String> frenchWords = mapper.readValue(frenchWordsNode.toString(),
                new TypeReference<List<String>>() {
                });
        Collections.shuffle(frenchWords);
        frenchWords = frenchWords.subList(0, 25);

        List<Word> wordsToReturn = new ArrayList<>();
        Random random = new Random();
        int redWordsLength = random.nextBoolean() ? 9 : 8;
        int blueWordsLength = 17 - redWordsLength;
        int blackWordsLength = 1;
        int whiteWordsLength = 7;

        for (int i = 0; i < redWordsLength; i++) {
            String wordName = frenchWords.remove(random.nextInt(frenchWords.size()));
            wordsToReturn.add(new Word(wordName, new ArrayList<>(), WordState.NOT_SELECTED,
                    WordColor.RED));
        }

        for (int i = 0; i < blueWordsLength; i++) {
            String wordName = frenchWords.remove(random.nextInt(frenchWords.size()));
            wordsToReturn.add(new Word(wordName, new ArrayList<>(), WordState.NOT_SELECTED,
                    WordColor.BLUE));
        }

        for (int i = 0; i < blackWordsLength; i++) {
            String wordName = frenchWords.remove(random.nextInt(frenchWords.size()));
            wordsToReturn.add(new Word(wordName, new ArrayList<>(), WordState.NOT_SELECTED,
                    WordColor.BLACK));
        }

        for (int i = 0; i < whiteWordsLength; i++) {
            String wordName = frenchWords.remove(random.nextInt(frenchWords.size()));
            wordsToReturn.add(new Word(wordName, new ArrayList<>(), WordState.NOT_SELECTED,
                    WordColor.WHITE));
        }

        Collections.shuffle(wordsToReturn);

        return wordsToReturn;
    }

    private PlayerTeam mapStringToPlayerTeam(String team) {
        switch (team) {
            case "RED":
                return PlayerTeam.RED;
            case "BLUE":
                return PlayerTeam.BLUE;
            default:
                return PlayerTeam.NONE;
        }
    }

    private PlayerRole mapStringToPlayerRole(String role) {
        switch (role) {
            case "SPYMASTER":
                return PlayerRole.SPYMASTER;
            case "OPERATIVE":
                return PlayerRole.OPERATIVE;
            default:
                return PlayerRole.NONE;
        }
    }
}
