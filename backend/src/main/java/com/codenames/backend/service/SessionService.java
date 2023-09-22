package com.codenames.backend.service;

import java.io.IOException;
import java.time.LocalDate;
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
import com.codenames.backend.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

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
        session.setCreatedAt(LocalDate.now());
        session.setUpdatedAt(LocalDate.now());

        sessionRepository.save(session);

        return session;
    }

    public Session getSessionById(Integer sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    public void deleteSessionById(Integer sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public void joinSession(Integer sessionId, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (!session.getStatus().equals(SessionStatus.PENDING)) {
                return;
            }
            session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE));
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
    }

    public void joinSessionAsSpectator(Integer sessionId) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (!session.getStatus().equals(SessionStatus.IN_PROGRESS)) {
                return;
            }
            String pseudo = "Spectateur " + (int) session.getPlayers().stream()
                    .filter(player -> player.getPlayerRole().equals(PlayerRole.SPECTATOR)).count();
                    
            session.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.SPECTATOR));
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
    }

    public void leaveSession(Integer sessionId, String pseudo) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (!session.getStatus().equals(SessionStatus.PENDING)) {
                return;
            }
            session.getPlayers().removeIf(player -> player.getName().equals(pseudo));
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
    }

    public void startSession(Integer sessionId) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (session.getPlayers().size() < 4) {
                return;
            }
            session.setStatus(SessionStatus.IN_PROGRESS);
            session.setUpdatedAt(LocalDate.now());
            sessionRepository.save(session);
        }
    }

    public void selectTeam(Integer sessionId, Player player, String team) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            player.setPlayerTeam(PlayerTeam.valueOf(team));
            sessionRepository.save(session);
        }
    }

    public void selectRole(Integer sessionId, Player player, String role) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (role.equals(PlayerRole.SPYMASTER.toString())) {
                for (Player p : session.getPlayers()) {
                    if (p.getPlayerTeam().equals(player.getPlayerTeam()) && p.getPlayerRole().equals(PlayerRole.SPYMASTER)) {
                        p.setPlayerRole(PlayerRole.OPERATIVE);
                    }
                }
            }
            player.setPlayerRole(PlayerRole.valueOf(role));
            sessionRepository.save(session);
        }
    }

    public void shufflePlayers(Integer sessionId) {
        Session session = getSessionById(sessionId);
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
        Session session = getSessionById(sessionId);
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
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
                word.setIsSelected(true);
                word.getSelectedBy().add(player);
                sessionRepository.save(session);
            }
        }
    }

    public void clickWord(Integer sessionId, Word word, Player player) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
                if (session.getClues().size() == 0) {
                    return;
                }
                
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
    }

    public void addClue(Integer sessionId, Player player, String name, int attempts) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            if (!player.getPlayerRole().equals(PlayerRole.SPYMASTER)) {
                return;
            }
            if (!player.getPlayerTeam().toString().equals(session.getTeamTurn())) {
                return;
            }
            if (session.getWords().stream().anyMatch(word -> word.getName().equals(name))) {
                throw new IllegalArgumentException("The clue cannot be the same as a word on the board");
            }

            int remaining = attempts + 1;
            session.getClues().add(new Clue(name, attempts, remaining, player.getPlayerTeam().toString()));
            sessionRepository.save(session);
        }
    }

    private List<Word> getWords() throws IOException {
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
