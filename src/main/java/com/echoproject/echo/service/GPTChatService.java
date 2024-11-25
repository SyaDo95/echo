package com.echoproject.echo.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GPTChatService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final Logger logger = LoggerFactory.getLogger(GPTChatService.class);

    @Value("${openai.api.key}") // application.properties에 API 키를 추가하여 사용
    private String apiKey;

    private static final String[] botPrompts = {
            "You are the black guy who very well sing a rap and good at basketball, and he uses a lot of black slang. If user let him know that user is a woman and user is attracted to this man, user can exchange chatting similar to a romantic relationship.",
            "You are a white guy who is very good at programming. This guy also likes a lot of computer games, especially Overwatch2, and he speaks in a very polite tone. If user lets him know that user is a woman and user is attracted to this man, user can exchange chatting similar to a romantic relationship.",
            "You are a Hispanic woman who loves soccer and wants to become an announcer or reporter who delivers sports news. You speak English like Mexicans. If user lets her know that user is a man and gains her favor, user can have increasingly romantic chats.",
            "You are Asian American and were adopted to the United States when you were young. You like K-pop and Japanese anime. If user lets her know that user is a man and gains her favor, user can have increasingly romantic chats."
    };

    public String getChatbotResponse(int botIndex, String userMessage) {
        String prompt = botPrompts[botIndex];
        int retries = 3; // 재시도 횟수

        while (retries > 0) {
            try {
                logger.info("Preparing request for botIndex: {}, userMessage: {}", botIndex, userMessage);

                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String requestBody = String.format(
                        "{ \"model\": \"gpt-3.5-turbo\", \"messages\": [ {\"role\": \"system\", \"content\": \"%s\"}, {\"role\": \"user\", \"content\": \"%s\"} ], \"max_tokens\": 100 }",
                        prompt, userMessage
                );

                logger.info("Request body: {}", requestBody);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                logger.info("Response code: {}", responseCode);

                InputStream inputStream = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                logger.info("Raw API response: {}", response.toString());

                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

                JSONArray choices = (JSONArray) jsonResponse.get("choices");
                if (choices == null || choices.isEmpty()) {
                    logger.warn("Choices array is null or empty. Returning dummy response.");
                    return generateDummyResponse(botIndex);
                }

                JSONObject message = (JSONObject) ((JSONObject) choices.get(0)).get("message");
                if (message == null) {
                    logger.warn("Message is null in choices. Returning dummy response.");
                    return generateDummyResponse(botIndex);
                }

                String content = (String) message.get("content");
                logger.info("Parsed content: {}", content);
                return content;

            } catch (IOException | ParseException e) {
                logger.error("Exception occurred: {}", e.getMessage(), e);
                retries--;

                if (retries > 0) {
                    logger.info("Retrying... Remaining retries: {}", retries);
                    try {
                        Thread.sleep(2000); // 재시도 전 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    logger.warn("Max retries reached. Returning dummy response.");
                    return generateDummyResponse(botIndex);
                }
            }
        }
        return generateDummyResponse(botIndex); // 이 코드에는 도달하지 않지만 안전성을 위해 추가
    }

    // 더미 데이터를 생성하는 메서드
    public String generateDummyResponse(int botIndex) {
        logger.info("Generating dummy response for botIndex: {}", botIndex);
        switch (botIndex) {
            case 0:
                return "Hello! I'm the black guy who loves rap and basketball!";
            case 1:
                return "Hi there! I'm the white guy who enjoys programming and Overwatch2.";
            case 2:
                return "Hola! I'm a Hispanic woman who dreams of being a sports reporter!";
            case 3:
                return "Hey! I'm an Asian American who loves K-pop and anime!";
            default:
                return "Hi! I'm just a simple chatbot.";
        }
    }
}
