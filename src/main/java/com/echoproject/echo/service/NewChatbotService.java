package com.echoproject.echo.service;

import com.echoproject.echo.dto.ChatbotConcept;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class NewChatbotService {

    @Value("${openai.api.key}") // OpenAI API 키를 application.properties에서 주입받음
    private String apiKey;

    private ChatbotConcept chatbotConcept;
    private boolean chatbotActive = false; // 챗봇이 활성 상태인지 여부

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    // 챗봇 컨셉 설정
    public void setChatbotConcept(ChatbotConcept chatbotConcept) {
        this.chatbotConcept = chatbotConcept;
        this.chatbotActive = true; // 챗봇 활성화
        System.out.println("New chatbot created with concept: " + chatbotConcept.toString());
        startChatLoop(); // 사용자 입력 루프 시작
    }

    // 사용자 입력에 따라 GPT 대화 생성
    public String generateChatResponse(String userInput) {
        if (chatbotConcept == null) {
            System.out.println("No chatbot concept is set.");
            return "No chatbot concept is set. Please create a chatbot first.";
        }

        // GPT 프롬프트에 name 추가
        String prompt = String.format(
                "You are a chatbot with the following characteristics: " +
                        "Name: %s, Job: %s, Age: %s, Hobby: %s, Favorite Food: %s, Favorite Color: %s. " +
                        "Respond to: %s",
                chatbotConcept.getName(), chatbotConcept.getJob(), chatbotConcept.getAge(),
                chatbotConcept.getHobby(), chatbotConcept.getFavoriteFood(), chatbotConcept.getFavoriteColor(), userInput
        );

        System.out.println("Prompt sent to OpenAI API: " + prompt);

        // GPT API 호출
        String apiResponse = callOpenAIAPI(prompt);
        System.out.println("Response from OpenAI API: " + apiResponse);

        return apiResponse;
    }

    private String callOpenAIAPI(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = String.format(
                    "{ \"model\": \"gpt-3.5-turbo\", \"messages\": [ {\"role\": \"system\", \"content\": \"%s\"}, {\"role\": \"user\", \"content\": \"%s\"} ], \"max_tokens\": 100 }",
                    prompt, prompt // 시스템 프롬프트와 사용자 메시지 동일
            );

            // 요청 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 읽기
            int responseCode = conn.getResponseCode();
            InputStream inputStream = responseCode >= 200 && responseCode < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // JSON 응답 파싱
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
            JSONArray choices = (JSONArray) jsonResponse.get("choices");

            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = (JSONObject) choices.get(0);
                JSONObject message = (JSONObject) choice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }

            return "No valid response from GPT.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while connecting to OpenAI API.";
        }
    }


    // 사용자 입력 루프 시작
    private void startChatLoop() {
        if (!chatbotActive) {
            System.out.println("Chatbot is not active. Set a chatbot concept first.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Chatbot is ready! Start chatting with your new chatbot. Type 'exit' to quit.");

        while (chatbotActive) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            // 사용자 입력 처리
            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Exiting chatbot. Goodbye!");
                chatbotActive = false;
                break;
            }

            // 챗봇 응답 생성
            String chatbotResponse = generateChatResponse(userInput);
            System.out.println("Chatbot: " + chatbotResponse);
        }

        scanner.close();
    }
}

