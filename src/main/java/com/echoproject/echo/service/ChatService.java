package com.echoproject.echo.service;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import com.echoproject.echo.repository.ChatHistoryRepository;
import com.echoproject.echo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ChatService {
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
            String botResponse = gptChatService.getChatbotResponse(botIndex, message);

            if (botResponse == null || botResponse.isEmpty()) {
                botResponse = gptChatService.generateDummyResponse(botIndex);
            }

            ChatHistory botChat = new ChatHistory();
            botChat.setUser(user);
            botChat.setBotIndex(botIndex);
            botChat.setMessage(botResponse);
            botChat.setSender("bot");

            try {
                // 디버깅 로그 추가
                System.out.println("Saving bot chat: " + botChat);
                chatHistoryRepository.save(botChat);
                System.out.println("Bot chat saved successfully: " + botChat);
            } catch (Exception e) {
                // 에러 발생 시 로그 출력
                System.err.println("Error saving bot chat: " + e.getMessage());
                e.printStackTrace();
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

