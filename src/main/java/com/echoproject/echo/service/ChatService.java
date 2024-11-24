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

    public void saveChatHistory(User user, int botIndex, String message, String sender) {
        // 사용자 메시지 저장
        ChatHistory userChat = new ChatHistory();
        userChat.setUser(user);
        userChat.setBotIndex(botIndex);
        userChat.setMessage(message);
        userChat.setSender(sender);
        chatHistoryRepository.save(userChat);

        // 봇 응답 저장
        if ("user".equals(sender)) {
            String botResponse = gptChatService.generateDummyResponse(botIndex);

            ChatHistory botChat = new ChatHistory();
            botChat.setUser(user);
            botChat.setBotIndex(botIndex);
            botChat.setMessage(botResponse);
            botChat.setSender("bot");

            // **저장 전 디버깅 로그 추가**
            logger.info("User assigned to bot chat: {}", botChat.getUser());
            logger.info("Bot index: {}", botChat.getBotIndex());
            logger.info("Bot message: {}", botChat.getMessage());
            logger.info("Sender: {}", botChat.getSender());
            logger.info("Created at: {}", botChat.getCreatedAt());

            try {
                // 데이터 저장
                chatHistoryRepository.save(botChat);
                logger.info("Bot chat saved successfully: {}", botChat);
            } catch (Exception e) {
                // 오류 발생 시 로그
                logger.error("Error saving bot chat: {}", e.getMessage(), e);
            }
        }
    }



    public String getBotResponse(String uid, int botIndex, String message) {
        User user = getOrCreateUser(uid);
        return gptChatService.getChatbotResponse(botIndex, message);
    }

    public List<ChatHistory> getChatHistory(User user) {
        return chatHistoryRepository.findByUser(user);
    }
}

