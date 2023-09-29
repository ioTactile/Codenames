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
import com.codenames.backend.model.Player;
import com.codenames.backend.model.Session;
import com.codenames.backend.model.Word;
import com.codenames.backend.service.SessionService;

@RestController
@RequestMapping("/room")
public class sessionController {

    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    public sessionController(SessionService sessionService, SimpMessagingTemplate messagingTemplate) {
        this.sessionService = sessionService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<Session> create(@RequestBody Map<String, String> requestPayload) {
        String pseudo = requestPayload.get("pseudo");
        Session session = sessionService.createSession(pseudo);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{id}")
    public Session get(@PathVariable("id") Long id) {
        return sessionService.getSessionById(id);
    }

    @GetMapping("/all")
    public List<Session> getAll() {
        return sessionService.getAllSessions();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        sessionService.deleteSessionById(id);
        return ResponseEntity.ok("Session deleted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> handleAction(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> requestPayload) {
        String action = (String) requestPayload.get("action");
        String pseudo = (String) requestPayload.get("pseudo");

        switch (action) {
            case "join":
                sessionService.joinSession(id, pseudo);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/join", pseudo);
                return ResponseEntity.ok("Session joined");
            case "leave":
                sessionService.leaveSession(id, pseudo);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/leave", pseudo);
                return ResponseEntity.ok("Session left");
            case "start":
                sessionService.startSession(id);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/start", id);
                return ResponseEntity.ok("Session started");
            case "shuffle-players":
                sessionService.shufflePlayers(id);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/shuffle-players", id);
                return ResponseEntity.ok("Players shuffled");
            case "select-team":
                Player player = (Player) requestPayload.get("player");
                String team = (String) requestPayload.get("team");
                sessionService.selectTeam(id, player, team);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/select-team", id);
                return ResponseEntity.ok("Team selected");
            case "select-role":
                player = (Player) requestPayload.get("player");
                String role = (String) requestPayload.get("role");
                sessionService.selectRole(id, player, role);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/select-role", id);
                return ResponseEntity.ok("Role selected");
            case "select-word":
                Word word = (Word) requestPayload.get("word");
                player = (Player) requestPayload.get("player");
                sessionService.selectWord(id, word, player);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/select-word", id);
                return ResponseEntity.ok("Word selected");
            case "click-word":
                word = (Word) requestPayload.get("word");
                player = (Player) requestPayload.get("player");
                sessionService.clickWord(id, word, player);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/click-word", id);
                return ResponseEntity.ok("Word clicked");
            case "add-clue":
                Clue clue = (Clue) requestPayload.get("clue");
                player = (Player) requestPayload.get("player");
                sessionService.addClue(id, clue, player);
                messagingTemplate.convertAndSend("/topic/session/" + id + "/add-clue", id);
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
