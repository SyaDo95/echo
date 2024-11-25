package com.echoproject.echo.service;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import com.echoproject.echo.repository.ChatHistoryRepository;
import com.echoproject.echo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private GPTChatService gptChatService;

    public User getOrCreateUser(String uid) {
        User user = userRepository.findByUid(uid);
        if (user == null) {
            user = new User();
            user.setUid(uid);
            userRepository.save(user);
        }
        return user;
    }

    public String getBotResponse(String uid, int botIndex, String message) {
        // 사용자 찾기 또는 생성
        User user = getOrCreateUser(uid);

        // 사용자 메시지 저장
        ChatHistory userChat = new ChatHistory();
        userChat.setUser(user);
        userChat.setBotIndex(botIndex);
        userChat.setMessage(message);
        userChat.setSender("user");

        try {
            chatHistoryRepository.save(userChat);
            logger.info("User message saved successfully: {}", userChat);
        } catch (Exception e) {
            logger.error("Error saving user message: {}", e.getMessage(), e);
        }

        // 봇 응답 생성
        String botResponse = gptChatService.getChatbotResponse(botIndex, message);

        // 봇 응답 저장
        if (botResponse != null && !botResponse.isEmpty()) {
            ChatHistory botChat = new ChatHistory();
            botChat.setUser(user);
            botChat.setBotIndex(botIndex);
            botChat.setMessage(botResponse);
            botChat.setSender("bot");

            try {
                chatHistoryRepository.save(botChat);
                logger.info("Bot response saved successfully: {}", botChat);
            } catch (Exception e) {
                logger.error("Error saving bot response: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("Bot response is empty. Skipping save.");
        }

        return botResponse;
    }

    public List<ChatHistory> getChatHistory(User user) {
        return chatHistoryRepository.findByUser(user);
    }
}

