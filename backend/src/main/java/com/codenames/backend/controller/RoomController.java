package com.codenames.backend.controller;

import java.util.ArrayList;
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

    @PostMapping("/create")
    public ResponseEntity<Room> create(@RequestBody Map<String, String> requestPayload) {
        String username = requestPayload.get("username");
        Room room = roomService.createRoom(username);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> get(@PathVariable("id") Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
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
                break;
            case "leave":
                roomService.leaveRoom(id, username);
                break;
            case "start":
                roomService.startRoom(id);
                break;
            case "shuffle-players":
                roomService.shufflePlayers(id);
                break;
            case "reset-players":
                roomService.resetPlayers(id);
                break;
            case "change-host":
                roomService.changeHost(id, username);
                break;
            case "select-team":
                roomService.selectTeam(id, team, username);
                break;
            case "select-role":
                roomService.selectRole(id, role, team, username);
                break;
            case "change-username":
                roomService.changeUsername(id, username, newUsername);
                break;
            case "manual-team-turn":
                roomService.manualTeamTurn(id, username);
                break;
            case "select-word":
                roomService.selectWord(id, wordname, username);
                break;
            case "click-word":
                roomService.clickWord(id, wordname, username);
                break;
            case "add-clue":
                ObjectMapper mapper = new ObjectMapper();
                Clue clue = mapper.convertValue(requestPayload.get("clue"), Clue.class);
                roomService.addClue(id, clue, username);
                break;
            case "replay":
                List<String> usernames = new ArrayList<String>();
                Object usernamesObj = requestPayload.get("usernames");
                if (usernamesObj instanceof List<?>) {
                    for (Object usernameObj : (List<?>) usernamesObj) {
                        if (usernameObj instanceof String) {
                            usernames.add((String) usernameObj);
                        }
                    }
                }
                roomService.replay(id, usernames);
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid action");
        }

        Room updatedRoom = roomService.getRoomById(id);
        sendRoomUpdate(updatedRoom);
        return ResponseEntity.ok(action + " : action completed");
    }

    private void sendRoomUpdate(Room room) {
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), room);
    }
}
