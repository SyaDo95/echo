package com.echoproject.echo.repository;

import com.echoproject.echo.entity.ChatHistory;
import com.echoproject.echo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUser(User user);

    // 추가: 특정 User와 botIndex에 따른 대화 기록 조회
    List<ChatHistory> findByUserAndBotIndex(User user, int botIndex);
}