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
        ChatHistory chat = new ChatHistory();
        chat.setUser(user);
        chat.setBotIndex(botIndex);
        chat.setMessage(message);
        chat.setSender(sender);
        chatHistoryRepository.save(chat);
    }

    public List<ChatHistory> getChatHistory(User user) {
        return chatHistoryRepository.findByUser(user);
    }
}
