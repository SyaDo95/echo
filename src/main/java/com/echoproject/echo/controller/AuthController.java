package com.echoproject.echo.controller;

import com.echoproject.echo.entity.User;
import com.echoproject.echo.service.ChatService;
import com.echoproject.echo.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private ChatService chatService;

    @PostMapping("/verifyToken")
    public String verifyToken(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            User user = chatService.getOrCreateUser(uid);
            return user.getUid(); // UID 반환
        } catch (IllegalArgumentException e) {
            return "Invalid token: " + e.getMessage();
        } catch (Exception e) {
            return "Error verifying token: " + e.getMessage();
        }
    }
}
