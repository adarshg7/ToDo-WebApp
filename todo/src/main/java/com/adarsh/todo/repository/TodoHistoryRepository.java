package com.adarsh.todo.repository;

import com.adarsh.todo.entity.TodoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoHistoryRepository extends JpaRepository<TodoHistory, Long> {
    
    List<TodoHistory> findByTodoIdOrderByChangedAtDesc(Long todoId);
}