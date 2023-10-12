package com.codenames.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;

    @BeforeEach
    public void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomService = new RoomService(roomRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        roomRepository = null;
        roomService = null;
    }

    @Test
    public void testCreateRoom() {
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        Room actualRoom = roomService.createRoom(pseudo);

        long redBlueWordsSum = expectedRoom.getWords().stream().filter(word -> word.getWordColor() == WordColor.RED)
                .count()
                + expectedRoom.getWords().stream().filter(word -> word.getWordColor() == WordColor.BLUE).count();

        assertEquals(expectedRoom.getWords().size(), 25);
        assertEquals(redBlueWordsSum, 17);
        assertEquals(expectedRoom.getPlayers().size(), actualRoom.getPlayers().size());
        assertEquals(expectedRoom.getWords().size(), actualRoom.getWords().size());
        assertNotEquals(expectedRoom.getWords(), actualRoom.getWords());
    }

    @Test
    void testGetRoomById() {
        Long roomId = 1L;
        Room expectedRoom = initializeRoom();
        expectedRoom.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));

        Room actualRoom = roomService.getRoomById(roomId);

        assertEquals(expectedRoom.getId(), actualRoom.getId());
    }

    @Test
    void testGetAllRooms() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(initializeRoom());
        expectedRooms.add(initializeRoom());

        when(roomRepository.findAll()).thenReturn(expectedRooms);

        List<Room> actualRooms = roomService.getAllRooms();

        assertEquals(expectedRooms.size(), actualRooms.size());
    }

    @Test
    void testDeleteRoomById() {
        Long roomId = 1L;
        Room expectedRoom = initializeRoom();
        expectedRoom.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.deleteRoomById(roomId);

        verify(roomRepository).findById(roomId);
        verify(roomRepository).deleteById(roomId);
    }

    @Test
    void testJoinRoom() {
        Long roomId = 1L;
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        String actualPseudo = "thomas";
        roomService.joinRoom(roomId, actualPseudo);

        assertEquals(expectedRoom.getPlayers().size(), 2);
        assertEquals(expectedRoom.getPlayers().get(1).getName(), actualPseudo);
        assertThrows(IllegalArgumentException.class, () -> roomService.joinRoom(roomId, pseudo));

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roomService.joinRoom(roomId, pseudo));
    }

    @Test
    void testJoinRoomAsSpectator() {
        Long roomId = 1L;
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.IN_PROGRESS);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.joinRoomAsSpectator(roomId);

        assertEquals(expectedRoom.getPlayers().size(), 2);
        assertEquals(expectedRoom.getPlayers().get(1).getName(), "Spectateur 1");
    }

    @Test
    void testLeaveRoom() {
        Long roomId = 1L;
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.leaveRoom(roomId, pseudo);

        assertEquals(expectedRoom.getPlayers().size(), 0);
    }

    @Test
    void testStartRoom() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        String pseudoThree = "julien";
        String pseudoFour = "pierre";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        expectedRoom.getPlayers().add(createPlayer(pseudoThree));
        expectedRoom.getPlayers().add(createPlayer(pseudoFour));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.startRoom(roomId);

        assertEquals(expectedRoom.getStatus(), RoomStatus.IN_PROGRESS);
    }

    @Test
    void testStartRoomWithNotEnoughPlayers() {
        Long roomId = 1L;
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));

        assertThrows(IllegalArgumentException.class, () -> roomService.startRoom(roomId));
    }

    @Test
    void testSelectTeam() {
        Long roomId = 1L;
        String pseudo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.BLUE);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.selectTeam(roomId, PlayerTeam.RED.toString(), pseudo);

        assertEquals(expectedRoom.getPlayers().get(0).getPlayerTeam(), PlayerTeam.RED);
        assertEquals(expectedRoom.getPlayers().get(0).getPlayerRole(), PlayerRole.OPERATIVE);

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.NONE);

        roomService.selectTeam(roomId, PlayerTeam.NONE.toString(), pseudo);

        assertEquals(expectedRoom.getPlayers().get(0).getPlayerTeam(), PlayerTeam.NONE);
        assertEquals(expectedRoom.getPlayers().get(0).getPlayerRole(), PlayerRole.NONE);
    }

    @Test
    void testSelectRole() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.SPYMASTER);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        assertThrows(IllegalArgumentException.class, () -> roomService.selectRole(roomId,
                PlayerRole.SPYMASTER.toString(), PlayerTeam.BLUE.toString(), pseudoTwo));

        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.NONE);

        roomService.selectRole(roomId, PlayerRole.NONE.toString(), PlayerTeam.BLUE.toString(), pseudo);

        assertEquals(expectedRoom.getPlayers().get(0).getPlayerRole(), PlayerRole.NONE);

    }

    @Test
    void testChangeUsername() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String newPseudo = "thomas";
        String newPseudoTwo = "jordan";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.changeUsername(roomId, pseudo, newPseudo);

        assertEquals(expectedRoom.getPlayers().get(0).getName(), newPseudo);

        assertThrows(IllegalArgumentException.class, () -> roomService.changeUsername(roomId,
                pseudo, newPseudoTwo));
    }

    @Test
    void testShufflePlayers() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        String pseudoThree = "julien";
        String pseudoFour = "pierre";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        expectedRoom.getPlayers().add(createPlayer(pseudoThree));
        expectedRoom.getPlayers().add(createPlayer(pseudoFour));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.shufflePlayers(roomId);

        assertEquals(expectedRoom.getPlayers().size(), 4);
        assertEquals(
                expectedRoom.getPlayers().stream().filter(player -> player.getPlayerTeam() == PlayerTeam.RED).count(),
                2);
        assertEquals(
                expectedRoom.getPlayers().stream().filter(player -> player.getPlayerTeam() == PlayerTeam.BLUE).count(),
                2);
    }

    @Test
    void testResetPlayers() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.OPERATIVE);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(1).setPlayerRole(PlayerRole.SPYMASTER);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.resetPlayers(roomId);

        assertEquals(expectedRoom.getPlayers().get(0).getPlayerTeam(), PlayerTeam.NONE);
        assertEquals(expectedRoom.getPlayers().get(0).getPlayerRole(), PlayerRole.NONE);
        assertEquals(expectedRoom.getPlayers().get(1).getPlayerTeam(), PlayerTeam.NONE);
        assertEquals(expectedRoom.getPlayers().get(1).getPlayerRole(), PlayerRole.NONE);
    }

    @Test
    void testChangeHost() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.changeHost(roomId, pseudoTwo);

        assertEquals(expectedRoom.getPlayers().get(0).getName(), pseudoTwo);
        assertEquals(expectedRoom.getPlayers().get(1).getName(), pseudo);
    }

    @Test
    void testManualTeamTurn() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.IN_PROGRESS);
        expectedRoom.setTeamTurn(PlayerTeam.RED.toString());

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.OPERATIVE);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(1).setPlayerRole(PlayerRole.SPYMASTER);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.manualTeamTurn(roomId, pseudo);

        assertEquals(expectedRoom.getTeamTurn(), PlayerTeam.BLUE.toString());
        assertThrows(IllegalArgumentException.class, () -> roomService.manualTeamTurn(roomId, pseudoTwo));
    }

    @Test
    void testSelectWord() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.IN_PROGRESS);
        expectedRoom.setTeamTurn(PlayerTeam.RED.toString());

        Word word = expectedRoom.getWords().get(0);
        Word wordClicked = expectedRoom.getWords().get(1);
        expectedRoom.getWords().get(1).setWordState(WordState.CLICKED);

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.OPERATIVE);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(1).setPlayerRole(PlayerRole.SPYMASTER);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.selectWord(roomId, word.getWordName(), pseudo);

        assertEquals(expectedRoom.getWords().get(0).getWordState(), WordState.SELECTED);
        assertThrows(IllegalArgumentException.class, () -> roomService.selectWord(roomId,
                "CAMION", pseudo));
        assertThrows(IllegalArgumentException.class, () -> roomService.selectWord(roomId,
                wordClicked.getWordName(), pseudo));
        assertThrows(IllegalArgumentException.class, () -> roomService.selectWord(roomId,
                word.getWordName(), pseudoTwo));
    }

    @Test
    void testClickWord() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        String pseudoThree = "julien";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        expectedRoom.getPlayers().add(createPlayer(pseudoThree));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.IN_PROGRESS);
        expectedRoom.setTeamTurn(PlayerTeam.RED.toString());

        Word word = expectedRoom.getWords().get(0);
        Word wordClicked = expectedRoom.getWords().get(1);
        expectedRoom.getWords().get(1).setWordState(WordState.CLICKED);

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.OPERATIVE);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(1).setPlayerRole(PlayerRole.SPYMASTER);
        expectedRoom.getPlayers().get(2).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(2).setPlayerRole(PlayerRole.SPYMASTER);

        expectedRoom.getClues().add(new Clue("TEST", 1, 2, pseudoThree));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.clickWord(roomId, word.getWordName(), pseudo);

        assertEquals(expectedRoom.getWords().get(0).getWordState(), WordState.CLICKED);
        assertThrows(IllegalArgumentException.class, () -> roomService.selectWord(roomId,
                "CAMION", pseudo));
        assertThrows(IllegalArgumentException.class, () -> roomService.clickWord(roomId,
                wordClicked.getWordName(), pseudo));
        assertThrows(IllegalArgumentException.class, () -> roomService.clickWord(roomId,
                word.getWordName(), pseudoTwo));
        assertThrows(IllegalArgumentException.class, () -> roomService.clickWord(roomId,
                word.getWordName(), pseudoThree));
    }

    @Test
    void testAddClue() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.IN_PROGRESS);
        expectedRoom.setTeamTurn(PlayerTeam.RED.toString());

        expectedRoom.getPlayers().get(0).setPlayerTeam(PlayerTeam.RED);
        expectedRoom.getPlayers().get(0).setPlayerRole(PlayerRole.SPYMASTER);
        expectedRoom.getPlayers().get(1).setPlayerTeam(PlayerTeam.BLUE);
        expectedRoom.getPlayers().get(1).setPlayerRole(PlayerRole.OPERATIVE);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.addClue(roomId, new Clue("TEST", 1, 2, pseudo), pseudo);

        assertEquals(expectedRoom.getClues().size(), 1);
        assertEquals(expectedRoom.getRoleTurn(), PlayerRole.OPERATIVE.toString());
        assertThrows(IllegalArgumentException.class, () -> roomService.addClue(roomId,
                new Clue("TEST", 1, 2, pseudoTwo), pseudoTwo));
        assertThrows(IllegalArgumentException.class, () -> roomService.addClue(roomId,
                new Clue(expectedRoom.getWords().get(0).getWordName(), 1, 2, pseudo), pseudo));
    }

    @Test
    void testReplay() {
        Long roomId = 1L;
        String pseudo = "jordan";
        String pseudoTwo = "thomas";
        Room expectedRoom = initializeRoom();
        expectedRoom.getPlayers().add(createPlayer(pseudo));
        expectedRoom.getPlayers().add(createPlayer(pseudoTwo));
        List<Word> words = initializeWords();
        expectedRoom.setWords(words);
        setTeamAndRoleTurn(expectedRoom, words);
        expectedRoom.setStatus(RoomStatus.RED_TEAM_WINS);

        List<String> pseudos = new ArrayList<>();
        pseudos.add(pseudo);
        pseudos.add(pseudoTwo);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(expectedRoom));
        when(roomRepository.save(expectedRoom)).thenReturn(expectedRoom);

        roomService.replay(roomId, pseudos);

        assertEquals(expectedRoom.getStatus(), RoomStatus.PENDING);
        assertEquals(expectedRoom.getPlayers().size(), 2);
    }

    private Room initializeRoom() {
        Room room = new Room();
        room.setPlayers(new ArrayList<>());
        room.setClues(new ArrayList<>());
        room.setIsBlackCardSelected(false);
        room.setStatus(RoomStatus.PENDING);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        return room;
    }

    private List<Word> initializeWords() {
        List<Word> words;
        try {
            words = getWords();
        } catch (IOException e) {
            e.printStackTrace();
            words = new ArrayList<>();
        }
        return words;
    }

    private Player createPlayer(String pseudo) {
        return new Player(pseudo, PlayerTeam.NONE, PlayerRole.NONE);
    }

    private void setTeamAndRoleTurn(Room room, List<Word> words) {
        int redCount = countWordsByColor(words, WordColor.RED);
        int blueCount = countWordsByColor(words, WordColor.BLUE);

        if (redCount > blueCount) {
            room.setTeamTurn(PlayerTeam.RED.toString());
        } else {
            room.setTeamTurn(PlayerTeam.BLUE.toString());
        }

        room.setRoleTurn(PlayerRole.SPYMASTER.toString());
        room.setRedRemainingWords(redCount);
        room.setBlueRemainingWords(blueCount);
    }

    private int countWordsByColor(List<Word> words, WordColor color) {
        return (int) words.stream().filter(word -> word.getWordColor() == color).count();
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
}
