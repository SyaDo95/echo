package com.echoproject.echo.service;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import com.echoproject.echo.repository.ChatHistoryRepository;
import com.echoproject.echo.repository.UserRepository;
import com.echoproject.echo.service.ChatService;
import com.echoproject.echo.service.GPTChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @MockBean
    private GPTChatService gptChatService; // GPTChatService를 Mock으로 설정

    @Test
    public void testSaveChatHistoryWithMockedGPT() {
        // Mock 설정: GPTChatService가 항상 가짜 데이터를 반환하도록 설정
        Mockito.when(gptChatService.getChatbotResponse(anyInt(), anyString()))
                .thenReturn("Mocked bot response");

        // Given: UID를 가진 사용자 생성 (또는 조회)
        String uid = "test-uid";
        User user = chatService.getOrCreateUser(uid);

        // When: 사용자 메시지 저장
        chatService.saveChatHistory(user, 1, "Hello, bot!", "user");

        // Then: 저장된 대화 내용 확인
        List<ChatHistory> history = chatService.getChatHistory(user);

        // Assertions
        assertNotNull(history);
        assertEquals(2, history.size()); // 사용자 메시지와 봇 응답
        assertEquals("Hello, bot!", history.get(0).getMessage());
        assertEquals("user", history.get(0).getSender());
        assertEquals("Mocked bot response", history.get(1).getMessage());
        assertEquals("bot", history.get(1).getSender());
    }
}
