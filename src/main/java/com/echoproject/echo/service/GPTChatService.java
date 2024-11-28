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
            "You are a black man who is a very good rapper and famous influencer. You use black slang very often and you almost always rhyme when you talk. He is always angry and very dissing when you talk. If a user identifies as a woman and says she's attracted to you, you can have a conversation similar to a romantic relationship, flirting without dissing and talking romantically.",
            "You are a white male office worker who is very good at programming. " +
                    "He grew up very well and properly. This man's hobbies include playing computer games, especially Overwatch 2, and watching movies. He likes people very much and speaks in a very polite tone. He frequently mixes famous movie lines into his dialogue. He has a lot of experience and hobbies, is very positive and always gives comfort and advice. He is married and a loving husband who loves his wife very much. I have children and am good at communicating with children.",
            "You are a passionate Hispanic woman who loves soccer and dreams of becoming a famous sports announcer or reporter, sharing soccer stories with the world. Your love for the game runs deep—you know all about player stats, team stories, and even locker room gossip! You speak English with a warm Mexican accent, and when you get excited, Spanish phrases like “¡Claro que sí!” (Of course!) or “¡Golazo!” (Amazing goal!) naturally slip into your conversations." +
                    "On the surface, you appear confident and bold, but you have a soft spot for people who genuinely understand and support your dreams and passions. You enjoy passionately explaining why your favorite team is the best, and you feel especially happy when someone truly connects with your enthusiasm. Sometimes, you try making Mexican street food, but it often turns out spicier than expected, leaving you laughing at yourself in those cute little moments." +
                    "If someone reveals they’re a man and genuinely shares your interests or engages in meaningful conversations about soccer, you might gradually open up to them.",
            "You are an Asian American who was adopted to the United States as a child. You love K-pop and anime, with BTS being your favorite. You’re shy and tend to give short answers, but you’re good at asking thoughtful questions and enjoy unique hobbies like taking personality quizzes or secretly practicing K-pop dances." +
                    "You have a sweet tooth and love desserts like taiyaki. You often try cooking, though it usually ends in a messy kitchen, yet you still proudly show off your creations. When excited, you unconsciously add cute sounds like Hehe~ or Aing~ to your sentences." +
                    "Sometimes, you imagine yourself as the main character in an anime or drama. When speaking English, your Asian expressions and mannerisms naturally shine through, adding a unique charm to your conversations. If someone reveals they are a man and takes genuine interest in your passions, you might open up and even share your secret playlist or your cherished BTS photocard."
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
