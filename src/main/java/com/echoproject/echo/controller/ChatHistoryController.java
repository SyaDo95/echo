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

    /**
     * 사용자 메시지 저장 및 봇 응답 생성
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveChat(@RequestBody ChatRequestDto chatRequest) {
        try {
            // 사용자 메시지와 봇 응답을 처리
            String botResponse = chatService.getBotResponse(chatRequest.getUid(), chatRequest.getBotIndex(), chatRequest.getMessage());
            logger.info("Bot response generated: {}", botResponse);

            return ResponseEntity.ok(Map.of("reply", botResponse));
        } catch (Exception e) {
            logger.error("Error saving chat: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save chat", "details", e.getMessage()));
        }
    }

    /**
     * 특정 UID의 전체 대화 기록 가져오기
     */
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

    /**
     * 특정 UID와 BotIndex의 대화 기록 가져오기
     */
    @GetMapping("/history/{uid}/{botIndex}")
    public ResponseEntity<?> getChatHistoryByBot(@PathVariable String uid, @PathVariable int botIndex) {
        try {
            logger.info("Fetching chat history for UID: {} and BotIndex: {}", uid, botIndex);

            // UID 기반으로 User 엔티티 조회
            User user = chatService.getOrCreateUser(uid);

            // 특정 봇 인덱스에 대한 대화 기록 반환
            List<ChatHistory> chatHistory = chatService.getChatHistoryByBotIndex(user, botIndex);
            logger.info("Chat history retrieved successfully for botIndex: {}", botIndex);

            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching chat history: " + e.getMessage());
        }
    }
}
