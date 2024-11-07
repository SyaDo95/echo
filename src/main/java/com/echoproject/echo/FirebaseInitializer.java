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
            Dotenv dotenv = Dotenv.load();
            String serviceAccountKey = dotenv.get("FIREBASE_SERVICE_ACCOUNT_KEY");

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
            e.printStackTrace();
        }
    }
}
