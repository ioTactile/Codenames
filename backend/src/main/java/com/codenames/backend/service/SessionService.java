package com.codenames.backend.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codenames.backend.model.Player;
import com.codenames.backend.model.PlayerTeam;
import com.codenames.backend.model.Session;
import com.codenames.backend.model.SessionStatus;
import com.codenames.backend.model.Word;
import com.codenames.backend.model.WordColor;
import com.codenames.backend.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(String pseudo) {
        Session session = new Session();

        session.setPlayers(new ArrayList<>());
        session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE));

        List<Word> words = new ArrayList<>();

        try {
            words = getWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.setWords(words);

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
        session.setCreatedAt(LocalDate.now());
        session.setUpdatedAt(LocalDate.now());

        sessionRepository.save(session);

        return session;
    }

    public Session joinSession(Integer sessionId, String pseudo) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE));
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
        return session;
    }

    public void startSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setStatus(SessionStatus.IN_PROGRESS);
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
    }

    public Session getSessionById(Integer sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    public void deleteSessionById(Integer sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public void selectTeam(Integer sessionId, String pseudo, String team) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.getPlayers().add(new Player(pseudo, PlayerTeam.valueOf(team)));
            sessionRepository.save(session);
        }
    }

    public void shufflePlayers(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
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
            }

            for (Player player : bluePlayers) {
                player.setPlayerTeam(PlayerTeam.BLUE);
            }

            session.getPlayers().clear();
            session.getPlayers().addAll(bluePlayers);
            session.getPlayers().addAll(redPlayers);
        }

        sessionRepository.save(session);
    }

    public void switchTeamTurn(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            if (session.getTeamTurn().equals(PlayerTeam.RED.toString())) {
                session.setTeamTurn(PlayerTeam.BLUE.toString());
            } else {
                session.setTeamTurn(PlayerTeam.RED.toString());
            }
            sessionRepository.save(session);
        }
    }

    public void selectWord(Integer sessionId, Word word, Player player) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
                word.setIsSelected(true);
                word.getSelectedBy().add(player);
                sessionRepository.save(session);
            }
        }
    }

    public void clickWord(Integer sessionId, Word word, Player player) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
                word.setIsClicked(true);

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
                    session.setUpdatedAt(LocalDate.now());
                } else if (session.getBlueRemainingWords() == 0) {
                    session.setStatus(SessionStatus.BLUE_TEAM_WINS);
                    session.setUpdatedAt(LocalDate.now());
                } else if (session.getIsBlackCard()) {
                    if (session.getTeamTurn().equals(PlayerTeam.RED.toString())) {
                        session.setStatus(SessionStatus.BLUE_TEAM_WINS);
                        session.setUpdatedAt(LocalDate.now());
                    } else {
                        session.setStatus(SessionStatus.RED_TEAM_WINS);
                        session.setUpdatedAt(LocalDate.now());
                    }
                }

                sessionRepository.save(session);
            }
        }
    }

    public List<Word> getWords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Word> words = mapper.readValue(
                getClass().getResourceAsStream("/words.json"),
                mapper.getTypeFactory().constructCollectionType(List.class, Word.class));

        List<Word> wordsToReturn = new ArrayList<Word>();
        Random random = new Random();
        int redWordsLength = random.nextBoolean() ? 9 : 8;
        int blueWordsLength = 17 - redWordsLength;

        for (int i = 0; i < redWordsLength; i++) {
            wordsToReturn.add(new Word(words.remove(
                    random.nextInt(words.size())).getName(), false, new ArrayList<>(), false, WordColor.RED));
        }

        for (int i = 0; i < blueWordsLength; i++) {
            wordsToReturn.add(new Word(words.remove(
                    random.nextInt(words.size())).getName(), false, new ArrayList<>(), false, WordColor.BLUE));
        }

        wordsToReturn.add(new Word(words.remove(
                random.nextInt(words.size())).getName(), false, new ArrayList<>(), false, WordColor.BLACK));

        for (Word word : words) {
            wordsToReturn.add(new Word(word.getName(), false, new ArrayList<>(), false, WordColor.WHITE));
        }

        Collections.shuffle(wordsToReturn);

        return wordsToReturn;
    }
}
