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
import com.codenames.backend.model.Room;
import com.codenames.backend.model.RoomStatus;
import com.codenames.backend.model.Word;
import com.codenames.backend.model.WordColor;
import com.codenames.backend.model.WordState;
import com.codenames.backend.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(String pseudo) {
        Room room = new Room();

        room.setPlayers(new ArrayList<>());
        room.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE));

        List<Word> words = new ArrayList<>();

        try {
            words = getWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        room.setWords(words);
        room.setClues(new ArrayList<>());

        int redCount = (int) words.stream().filter(word -> word.getWordColor() == WordColor.RED).count();
        int blueCount = (int) words.stream().filter(word -> word.getWordColor() == WordColor.BLUE).count();

        if (redCount > blueCount) {
            room.setTeamTurn(PlayerTeam.RED.toString());
        } else {
            room.setTeamTurn(PlayerTeam.BLUE.toString());
        }

        room.setRedRemainingWords(redCount);
        room.setBlueRemainingWords(blueCount);
        room.setIsBlackCardSelected(false);
        room.setStatus(RoomStatus.PENDING);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        roomRepository.save(room);

        return room;
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public void deleteRoomById(Long roomId) {
        boolean exists = roomRepository.existsById(roomId);
        if (!exists) {
            throw new IllegalStateException("Room with id " + roomId + " does not exist");
        }
        roomRepository.deleteById(roomId);
    }

    public void joinRoom(Long roomId, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        room.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE));
        roomRepository.save(room);
    }

    public void joinRoomAsSpectator(Long roomId) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        String pseudo = "Spectateur " + (int) room.getPlayers().stream()
                .filter(player -> player.getPlayerRole().equals(PlayerRole.SPECTATOR)).count();
        room.getPlayers().add(new Player(pseudo, PlayerTeam.NONE, PlayerRole.SPECTATOR));
        roomRepository.save(room);
    }

    public void leaveRoom(Long roomId, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        room.getPlayers().removeIf(player -> player.getName().equals(pseudo));
        roomRepository.save(room);
    }

    public void startRoom(Long roomId) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        if (room.getPlayers().size() < 4) {
            throw new IllegalArgumentException("Not enough players");
        }
        room.setStatus(RoomStatus.IN_PROGRESS);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }

    public void selectTeam(Long roomId, String team, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }
        player.setPlayerTeam(mapStringToPlayerTeam(team));
        roomRepository.save(room);
    }

    public void selectRole(Long roomId, String role, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }
        if (role.equals(PlayerRole.SPYMASTER.toString())) {
            for (Player p : room.getPlayers()) {
                if (p.getPlayerTeam().equals(player.getPlayerTeam())
                        && p.getPlayerRole().equals(PlayerRole.SPYMASTER)) {
                    p.setPlayerRole(PlayerRole.OPERATIVE);
                }
            }
        }
        player.setPlayerRole(mapStringToPlayerRole(role));
        roomRepository.save(room);
    }

    public void shufflePlayers(Long roomId) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.PENDING)) {
            throw new IllegalArgumentException("Room not found or not pending");
        }
        List<Player> players = room.getPlayers();
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
        room.getPlayers().clear();
        room.getPlayers().addAll(bluePlayers);
        room.getPlayers().addAll(redPlayers);
        roomRepository.save(room);
    }

    public void switchTeamTurn(Long roomId) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        if (room.getTeamTurn().equals(PlayerTeam.RED.toString())) {
            room.setTeamTurn(PlayerTeam.BLUE.toString());
        } else {
            room.setTeamTurn(PlayerTeam.RED.toString());
        }
        roomRepository.save(room);
    }

    public void manualTeamTurn(Long roomId, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null ||
                !player.getPlayerRole().equals(PlayerRole.OPERATIVE) ||
                !player.getPlayerTeam().toString().equals(room.getTeamTurn())) {
            throw new IllegalArgumentException("Player not found, not operative or not on the right team");
        }
        switchTeamTurn(roomId);
        roomRepository.save(room);
    }

    public void selectWord(Long roomId, String wordName, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null || !player.getPlayerRole().equals(PlayerRole.OPERATIVE)) {
            throw new IllegalArgumentException("Player not found or not operative");
        }
        Word word = room.getWords().stream()
                .filter(w -> w.getWordName().equals(wordName)).findFirst().orElse(null);
        if (word == null) {
            throw new IllegalArgumentException("Word not found");
        }
        if (player.getPlayerTeam().toString().equals(room.getTeamTurn())) {
            if (word.getWordState().equals(WordState.SELECTED)) {
                word.setWordState(WordState.NOT_SELECTED);
                word.getSelectedBy().remove(player.getName());
            }
            word.setWordState(WordState.SELECTED);
            word.getSelectedBy().add(player.getName());
            roomRepository.save(room);
        } else {
            throw new IllegalArgumentException("Player is not on the right team");
        }
    }

    public void clickWord(Long roomId, String wordName, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null || !player.getPlayerRole().equals(PlayerRole.OPERATIVE)) {
            throw new IllegalArgumentException("Player not found or not operative");
        }
        Word word = room.getWords().stream()
                .filter(w -> w.getWordName().equals(wordName)).findFirst().orElse(null);
        if (word == null) {
            throw new IllegalArgumentException("Word not found");
        }
        if (player.getPlayerTeam().toString().equals(room.getTeamTurn())) {
            if (room.getClues().size() == 0) {
                return;
            }
            word.setWordState(WordState.CLICKED);
            if (word.getWordColor() == WordColor.RED) {
                room.setRedRemainingWords(room.getRedRemainingWords() - 1);
            } else if (word.getWordColor() == WordColor.BLUE) {
                room.setBlueRemainingWords(room.getBlueRemainingWords() - 1);
            } else if (word.getWordColor() == WordColor.WHITE) {
                switchTeamTurn(roomId);
            } else if (word.getWordColor() == WordColor.BLACK) {
                room.setIsBlackCardSelected(true);
            }
            if (room.getRedRemainingWords() == 0) {
                room.setStatus(RoomStatus.RED_TEAM_WINS);
                room.setUpdatedAt(LocalDateTime.now());
            } else if (room.getBlueRemainingWords() == 0) {
                room.setStatus(RoomStatus.BLUE_TEAM_WINS);
                room.setUpdatedAt(LocalDateTime.now());
            } else if (room.getIsBlackCardSelected()) {
                if (room.getTeamTurn().equals(PlayerTeam.RED.toString())) {
                    room.setStatus(RoomStatus.BLUE_TEAM_WINS);
                    room.setUpdatedAt(LocalDateTime.now());
                } else {
                    room.setStatus(RoomStatus.RED_TEAM_WINS);
                    room.setUpdatedAt(LocalDateTime.now());
                }
            }
            if (room.getClues().size() > 0) {
                Clue clue = room.getClues().get(room.getClues().size() - 1);
                clue.setRemaining(clue.getRemaining() - 1);
                if (clue.getRemaining() == 0) {
                    switchTeamTurn(roomId);
                }
            }
            roomRepository.save(room);
        } else {
            throw new IllegalArgumentException("Player is not on the right team");
        }
    }

    public void addClue(Long roomId, Clue clue, String pseudo) {
        Room room = getRoomById(roomId);
        if (room == null || !room.getStatus().equals(RoomStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Room not found or not in progress");
        }
        Player player = room.getPlayers().stream()
                .filter(p -> p.getName().equals(pseudo)).findFirst().orElse(null);
        if (player == null || !player.getPlayerRole().equals(PlayerRole.SPYMASTER)) {
            throw new IllegalArgumentException("Player not found or not spymaster");
        }
        if (!player.getPlayerTeam().toString().equals(room.getTeamTurn())) {
            throw new IllegalArgumentException("Player is not spymaster or not on the right team");
        }
        if (room.getWords().stream().anyMatch(word -> word.getWordName().equals(clue.getClueName()))) {
            throw new IllegalArgumentException("Clue name is a word from the board");
        }
        room.getClues().add(new Clue(clue.getClueName(), clue.getAttempts(), clue.getAttempts(), player.getName()));
        roomRepository.save(room);
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
        int blueWordsLength = redWordsLength == 9 ? 8 : 9;
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