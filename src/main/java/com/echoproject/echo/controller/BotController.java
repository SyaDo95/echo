package com.echoproject.echo.controller;

import com.echoproject.echo.service.GPTChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class BotController {

    @Autowired
    private GPTChatService gptChatService;

    // BotController.java에서 로그 추가
    @PostMapping("/create-bot")
    public ResponseEntity<?> createBot(@RequestBody Map<String, List<String>> request) {
        List<String> selections = request.get("selections");
        if (selections == null || selections.size() != 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid selections data."));
        }

        String botPrompt = "Bot created with selections: " + selections.toString();
        return ResponseEntity.ok(Map.of("message", botPrompt));
    }

    @GetMapping("/create-bot")
    public ResponseEntity<?> handleGetRequest() {
        return ResponseEntity.badRequest().body("GET method is not supported for this endpoint. Use POST instead.");
    }


}

