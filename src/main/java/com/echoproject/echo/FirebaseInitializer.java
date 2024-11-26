package com.echoproject.echo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class FirebaseInitializer {

    public static void initialize() {
        try {
            // .env 파일에서 환경 변수 불러오기
            Dotenv dotenv = Dotenv.configure()
                    .directory("./src/main/resources") // 작업 디렉토리 기준 상대 경로
                    .filename(".env")
                    .load();

            // JSON 데이터를 문자열로 가져오기
            String serviceAccountKey = dotenv.get("FIREBASE_API_KEY");
            if (serviceAccountKey == null || serviceAccountKey.isEmpty()) {
                throw new IllegalStateException("FIREBASE_API_KEY is not set in the .env file or cannot be read");
            }

            // 줄바꿈 이스케이프 처리
            serviceAccountKey = serviceAccountKey.replace("\\\\n", "\n");

            // 환경 변수에서 가져온 JSON 문자열을 InputStream으로 변환
            InputStream serviceAccount = new ByteArrayInputStream(serviceAccountKey.getBytes());

            // Firebase 옵션 설정
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Firebase 앱 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
