package com.codenames.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codenames.backend.model.Clue;
import com.codenames.backend.model.Room;
import com.codenames.backend.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    private void sendRoomUpdate(Room room) {
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), room);
    }

    @PostMapping("/create")
    public ResponseEntity<Room> create(@RequestBody Map<String, String> requestPayload) {
        String username = requestPayload.get("username");
        Room room = roomService.createRoom(username);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{id}")
    public Room get(@PathVariable("id") Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/all")
    public List<Room> getAll() {
        return roomService.getAllRooms();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.ok("Room deleted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> handleAction(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> requestPayload) {

        String action = (String) requestPayload.get("action");
        String username = (String) requestPayload.get("username");
        String newUsername = (String) requestPayload.get("newUsername");
        String wordname = (String) requestPayload.get("wordname");
        String role = (String) requestPayload.get("role");
        String team = (String) requestPayload.get("team");

        switch (action) {
            case "join":
                roomService.joinRoom(id, username);
                Room roomJoin = roomService.getRoomById(id);
                sendRoomUpdate(roomJoin);
                return ResponseEntity.ok("Room joined");
            case "leave":
                roomService.leaveRoom(id, username);
                Room roomLeave = roomService.getRoomById(id);
                sendRoomUpdate(roomLeave);
                return ResponseEntity.ok("Room left");
            case "start":
                roomService.startRoom(id);
                Room roomStart = roomService.getRoomById(id);
                sendRoomUpdate(roomStart);
                return ResponseEntity.ok("Room started");
            case "shuffle-players":
                roomService.shufflePlayers(id);
                Room roomShuffle = roomService.getRoomById(id);
                sendRoomUpdate(roomShuffle);
                return ResponseEntity.ok("Players shuffled");
            case "reset-players":
                roomService.resetPlayers(id);
                Room roomReset = roomService.getRoomById(id);
                sendRoomUpdate(roomReset);
                return ResponseEntity.ok("Players reset");
            case "select-team":
                roomService.selectTeam(id, team, username);
                Room roomSelectTeam = roomService.getRoomById(id);
                sendRoomUpdate(roomSelectTeam);
                return ResponseEntity.ok("Team selected");
            case "select-role":
                roomService.selectRole(id, role, team, username);
                Room roomSelectRole = roomService.getRoomById(id);
                sendRoomUpdate(roomSelectRole);
                return ResponseEntity.ok("Role selected");
            case "change-username":
                roomService.changeUsername(id, username, newUsername);
                Room roomChangeUsername = roomService.getRoomById(id);
                sendRoomUpdate(roomChangeUsername);
                return ResponseEntity.ok("Username changed");
            case "manual-team-turn":
                roomService.manualTeamTurn(id, username);
                Room roomManualTeamTurn = roomService.getRoomById(id);
                sendRoomUpdate(roomManualTeamTurn);
                return ResponseEntity.ok("Team turn changed");
            case "select-word":
                roomService.selectWord(id, wordname, username);
                Room roomSelectWord = roomService.getRoomById(id);
                sendRoomUpdate(roomSelectWord);
                return ResponseEntity.ok("Word selected");
            case "click-word":
                roomService.clickWord(id, wordname, username);
                Room roomClickWord = roomService.getRoomById(id);
                sendRoomUpdate(roomClickWord);
                return ResponseEntity.ok("Word clicked");
            case "add-clue":
                ObjectMapper mapper = new ObjectMapper();
                Clue clue = mapper.convertValue(requestPayload.get("clue"), Clue.class);
                roomService.addClue(id, clue, username);
                Room roomAddClue = roomService.getRoomById(id);
                sendRoomUpdate(roomAddClue);
                return ResponseEntity.ok("Clue added");
            default:
                return ResponseEntity.badRequest().body("Invalid action");
        }
    }
}
