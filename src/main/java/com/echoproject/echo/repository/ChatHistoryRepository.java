package com.echoproject.echo.repository;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUser(User user);
}
