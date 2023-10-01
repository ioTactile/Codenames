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
        String wordname = (String) requestPayload.get("wordname");
        String role = (String) requestPayload.get("role");
        String team = (String) requestPayload.get("team");

        switch (action) {
            case "join":
                roomService.joinRoom(id, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/join", username);
                return ResponseEntity.ok("Room joined");
            case "leave":
                roomService.leaveRoom(id, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/leave", username);
                return ResponseEntity.ok("Room left");
            case "start":
                roomService.startRoom(id);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/start", id);
                return ResponseEntity.ok("Room started");
            case "shuffle-players":
                roomService.shufflePlayers(id);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/shuffle-players", id);
                return ResponseEntity.ok("Players shuffled");
            case "select-team":
                roomService.selectTeam(id, team, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/select-team", id);
                return ResponseEntity.ok("Team selected");
            case "select-role":
                roomService.selectRole(id, role, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/select-role", id);
                return ResponseEntity.ok("Role selected");
            case "manual-team-turn":
                roomService.manualTeamTurn(id, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/manual-team-turn", id);
                return ResponseEntity.ok("Team turn changed");
            case "select-word":
                roomService.selectWord(id, wordname, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/select-word", id);
                return ResponseEntity.ok("Word selected");
            case "click-word":
                roomService.clickWord(id, wordname, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/click-word", id);
                return ResponseEntity.ok("Word clicked");
            case "add-clue":
                ObjectMapper mapper = new ObjectMapper();
                Clue clue = mapper.convertValue(requestPayload.get("clue"), Clue.class);
                roomService.addClue(id, clue, username);
                messagingTemplate.convertAndSend("/topic/room/" + id + "/add-clue", id);
                return ResponseEntity.ok("Clue added");
            default:
                return ResponseEntity.badRequest().body("Invalid action");
        }
    }

    // @PutMapping("/{id}/join")
    // public ResponseEntity<String> join(@PathVariable("id") Long id, @RequestBody
    // String pseudo) {
    // sessionService.joinSession(id, pseudo);
    // messagingTemplate.convertAndSend("/topic/session/" + id + "/join", pseudo);
    // return ResponseEntity.ok("Session joined");
    // }

    // @PutMapping("/{id}/leave")
    // public ResponseEntity<String> leave(@PathVariable("id") Long id, @RequestBody
    // String pseudo) {
    // sessionService.leaveSession(id, pseudo);
    // messagingTemplate.convertAndSend("/topic/session/" + id + "/leave", pseudo);
    // return ResponseEntity.ok("Session left");
    // }

    // @PutMapping("/{id}/start")
    // public ResponseEntity<String> start(@PathVariable("id") Long id) {
    // sessionService.startSession(id);
    // messagingTemplate.convertAndSend("/topic/session/" + id + "/start", id);
    // return ResponseEntity.ok("Session started");
    // }

    // @PutMapping("/{id}/shuffle-players")
    // public ResponseEntity<String> shufflePlayers(@PathVariable("id") Long id) {
    // sessionService.shufflePlayers(id);
    // return ResponseEntity.ok("Players shuffled");
    // }

    // @PutMapping("/{id}/select-team")
    // public ResponseEntity<String> selectTeam(@PathVariable("id") Long id,
    // @RequestBody Player player, @RequestBody String team) {
    // sessionService.selectTeam(id, player, team);
    // return ResponseEntity.ok("Team selected");
    // }

    // @PutMapping("/{id}/select-role")
    // public ResponseEntity<String> selectRole(@PathVariable("id") Long id,
    // @RequestBody Player player, @RequestBody String role) {
    // sessionService.selectRole(id, player, role);
    // return ResponseEntity.ok("Role selected");
    // }

    // @PutMapping("/{id}/select-word")
    // public ResponseEntity<String> selectWord(@PathVariable("id") Long id,
    // @RequestBody Word word, @RequestBody Player player) {
    // sessionService.selectWord(id, word, player);
    // return ResponseEntity.ok("Word selected");
    // }

    // @PutMapping("/{id}/click-word")
    // public ResponseEntity<String> clickWord(@PathVariable("id") Long id,
    // @RequestBody Word word, @RequestBody Player player) {
    // sessionService.clickWord(id, word, player);
    // return ResponseEntity.ok("Word clicked");
    // }

    // @PutMapping("/{id}/add-clue")
    // public ResponseEntity<String> addClue(@PathVariable("id") Long id,
    // @RequestBody Clue clue, @RequestBody Player player) {
    // sessionService.addClue(id, clue, player);
    // return ResponseEntity.ok("Clue added");
    // }
}
