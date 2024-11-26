package com.echoproject.echo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history") // 소문자 테이블명으로 수정
@Data
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key, Auto Increment

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign Key (연결된 User)

    @Column(nullable = false)
    private int botIndex; // bot_index (not null)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message; // message (TEXT, not null)

    @Column(nullable = false)
    private String sender; // sender (varchar(255), not null)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 기본값 설정
}
