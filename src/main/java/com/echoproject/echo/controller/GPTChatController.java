package com.echoproject.echo.controller;

import com.echoproject.echo.service.GPTChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")  // React 개발 서버 주소
public class GPTChatController {

    @Autowired
    private GPTChatService gptChatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> getChatResponse(@RequestBody Map<String, Object> request) {
        int botIndex = (int) request.get("botIndex");
        String userMessage = (String) request.get("userMessage");
        List<String> userSelections = (List<String>) request.get("userSelections"); // 추가된 사용자 선택

        String botResponse;

        // botIndex가 -1이면 동적 프롬프트를 생성
        if (botIndex == -1) {
            botResponse = gptChatService.getChatbotResponse(botIndex, userMessage, userSelections);
        } else {
            botResponse = gptChatService.getChatbotResponse(botIndex, userMessage, null);
        }

        Map<String, String> response = new HashMap<>();
        response.put("reply", botResponse);
        return ResponseEntity.ok(response);
    }
}
