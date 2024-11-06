// src/main/java/com/echoproject/echo/service/GPTChatService.java
package com.echoproject.echo.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GPTChatService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

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
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = "{\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"" + prompt + "\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + userMessage + "\"}\n" +
                    "  ],\n" +
                    "  \"max_tokens\": 100\n" +
                    "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
            JSONArray choices = (JSONArray) jsonResponse.get("choices");
            JSONObject message = (JSONObject) ((JSONObject) choices.get(0)).get("message");
            return (String) message.get("content");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return "Error occurred while communicating with GPT.";
        }
    }
}
