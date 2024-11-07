// src/main/java/com/echoproject/echo/service/FirebaseAuthService.java
package com.echoproject.echo.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {
    public FirebaseToken verifyIdToken(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 오류 처리 필요 시 예외를 던질 수도 있습니다.
        }
    }
}
