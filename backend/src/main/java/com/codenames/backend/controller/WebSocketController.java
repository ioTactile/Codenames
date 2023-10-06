package com.codenames.backend.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.codenames.backend.model.Room;
import com.codenames.backend.service.RoomService;

@Controller
public class WebSocketController {

    private final RoomService roomService;

    public WebSocketController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/update")
    @SendTo("/topic/room/{roomId}")
    public Room getRoom(@DestinationVariable Long roomId) {
        Room room = roomService.getRoomById(roomId);
        return room;
    }
}
