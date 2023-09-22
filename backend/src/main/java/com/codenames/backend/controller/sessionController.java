package com.codenames.backend.controller;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codenames.backend.model.Player;
import com.codenames.backend.model.Session;
import com.codenames.backend.service.SessionService;

@RestController
@RequestMapping("/room")
public class sessionController {
    
    private final SessionService sessionService;

    public sessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Session> create(@RequestBody String pseudo) {
        Logger.getLogger("create session request: {}", pseudo);
        return ResponseEntity.ok(sessionService.createSession(pseudo));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> join(@PathVariable Integer id, @RequestBody String pseudo) {
        return ResponseEntity.ok("Session joined");
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<String> leave(@PathVariable Integer id, @RequestBody String pseudo) {
        return ResponseEntity.ok("Session left");
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> start(@PathVariable Integer id) {
        return ResponseEntity.ok("Session started");
    }

    @PostMapping("/{id}/shuffle-players")
    public ResponseEntity<String> shufflePlayers(@PathVariable Integer id) {
        return ResponseEntity.ok("Players shuffled");
    }
    
    @PostMapping("/{id}/select-team")
    public ResponseEntity<String> selectTeam(@PathVariable Integer id, @RequestBody Player player, String team) {
        return ResponseEntity.ok("Team selected");
    }

    @PostMapping("/{id}/select-role")
    public ResponseEntity<String> selectRole(@PathVariable Integer id, @RequestBody Player player, String role) {
        return ResponseEntity.ok("Role selected");
    }
}
