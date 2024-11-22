package com.echoproject.echo.controller;

import com.echoproject.echo.entity.User;
import com.echoproject.echo.service.ChatService;
import com.echoproject.echo.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private ChatService chatService;

    @PostMapping("/verifyToken")
    public String verifyToken(@RequestBody String idToken) {
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
        if (decodedToken != null) {
            String uid = decodedToken.getUid();
            // UID로 사용자 생성 또는 조회
            User user = chatService.getOrCreateUser(uid);
            return user.getUid(); // UID 반환
        } else {
            return "Invalid token";
        }
    }
}
