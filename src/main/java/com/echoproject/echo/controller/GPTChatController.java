// src/main/java/com/echoproject/echo/controller/GPTChatController.java
package com.echoproject.echo.controller;

import com.echoproject.echo.service.GPTChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

        String botResponse = gptChatService.getChatbotResponse(botIndex, userMessage);

        Map<String, String> response = new HashMap<>();
        response.put("reply", botResponse);
        return ResponseEntity.ok(response);
    }
}
