package com.echoproject.echo.controller;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import com.echoproject.echo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/save")
    public String saveChat(@RequestBody ChatHistory chatRequest) {
        User user = chatService.getOrCreateUser(chatRequest.getUser().getUid());
        chatService.saveChatHistory(user, chatRequest.getBotIndex(), chatRequest.getMessage(), chatRequest.getSender());
        return "Chat saved successfully";
    }

    @GetMapping("/history/{uid}")
    public List<ChatHistory> getChatHistory(@PathVariable String uid) {
        User user = chatService.getOrCreateUser(uid);
        return chatService.getChatHistory(user);
    }
}
