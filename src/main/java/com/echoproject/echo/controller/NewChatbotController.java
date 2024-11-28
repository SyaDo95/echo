package com.echoproject.echo.controller;

import com.echoproject.echo.dto.ChatbotConcept;
import com.echoproject.echo.service.NewChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newchatbot")
@CrossOrigin(origins = "http://localhost:3000")
public class NewChatbotController {

    private final NewChatbotService newChatbotService;

    public NewChatbotController(NewChatbotService newChatbotService) {
        this.newChatbotService = newChatbotService;
    }

    // 새로운 챗봇 생성
    @PostMapping("/create")
    public ResponseEntity<String> createChatbot(@RequestBody ChatbotConcept chatbotConcept) {
        System.out.println("Received Chatbot Concept: " + chatbotConcept); // 요청 데이터 확인
        newChatbotService.setChatbotConcept(chatbotConcept);
        return ResponseEntity.ok("Chatbot concept set successfully!");
    }

    // 사용자 입력에 따른 대화 생성
    @PostMapping("/chat")
    public ResponseEntity<String> chatWithChatbot(@RequestBody Map<String, String> requestBody) {
        String userInput = requestBody.get("userInput");
        System.out.println("Received user input: " + userInput);

        // 서비스에서 GPT 응답 생성
        String response = newChatbotService.generateChatResponse(userInput);
        System.out.println("Chatbot response: " + response);
        return ResponseEntity.ok(response);
    }
}


