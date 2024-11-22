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
        try {
            // UID 기반으로 User 엔티티 조회 또는 생성
            User user = chatService.getOrCreateUser(chatRequest.getUser().getUid());
            // 대화 기록 저장
            chatService.saveChatHistory(user, chatRequest.getBotIndex(), chatRequest.getMessage(), chatRequest.getSender());
            return "Chat saved successfully";
        } catch (Exception e) {
            return "Error saving chat: " + e.getMessage();
        }
    }

    @GetMapping("/history/{uid}")
    public List<ChatHistory> getChatHistory(@PathVariable String uid) {
        try {
            // UID 기반으로 User 엔티티 조회
            User user = chatService.getOrCreateUser(uid);
            // 해당 사용자의 대화 기록 반환
            return chatService.getChatHistory(user);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching chat history: " + e.getMessage());
        }
    }
}
