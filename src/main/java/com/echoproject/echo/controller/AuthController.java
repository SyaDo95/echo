// src/main/java/com/echoproject/echo/controller/AuthController.java
package com.echoproject.echo.controller;

import com.echoproject.echo.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @PostMapping("/verifyToken")
    public String verifyToken(@RequestBody String idToken) {
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
        if (decodedToken != null) {
            return "Authenticated user: " + decodedToken.getUid();
        } else {
            return "Invalid token";
        }
    }
}
