package com.echoproject.echo.controller;

import com.echoproject.echo.dto.ChatRequestDto;
import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import com.echoproject.echo.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping("/save")
    public ResponseEntity<?> saveChat(@RequestBody ChatRequestDto chatRequest) {
        try {
            User user = chatService.getOrCreateUser(chatRequest.getUid());
            chatService.saveChatHistory(user, chatRequest.getBotIndex(), chatRequest.getMessage(), chatRequest.getSender());

            String botResponse = chatService.getBotResponse(chatRequest.getUid(), chatRequest.getBotIndex(), chatRequest.getMessage());
            logger.info("Bot response generated: {}", botResponse);

            return ResponseEntity.ok(Map.of("reply", botResponse));
        } catch (Exception e) {
            logger.error("Error saving chat: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save chat", "details", e.getMessage()));
        }
    }



    @GetMapping("/history/{uid}")
    public ResponseEntity<?> getChatHistory(@PathVariable String uid) {
        try {
            logger.info("Fetching chat history for UID: {}", uid);

            // UID 기반으로 User 엔티티 조회
            User user = chatService.getOrCreateUser(uid);

            // 해당 사용자의 대화 기록 반환
            List<ChatHistory> chatHistory = chatService.getChatHistory(user);
            logger.info("Chat history retrieved successfully");

            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching chat history: " + e.getMessage());
        }
    }
}
