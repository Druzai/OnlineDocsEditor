package ru.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.app.components.WSMessage;
import ru.app.services.DocumentService;

@Controller
public class WebSocketController {
    @Autowired
    private DocumentService documentService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public WSMessage greeting(WSMessage message) {
        documentService.editDocument(message);
        return message;
    }
}
