package com.adarsh.todo.repository;

import com.adarsh.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    List<Todo> findByCompleted(Boolean completed);
    
    List<Todo> findByCreatedBy(String createdBy);
    
    Page<Todo> findByCreatedBy(String createdBy, Pageable pageable);
    
    Page<Todo> findByCompletedAndCreatedBy(Boolean completed, String createdBy, Pageable pageable);
    
    @Query("SELECT t FROM Todo t WHERE t.createdBy = :createdBy AND t.priority = :priority ORDER BY t.dueDate ASC")
    List<Todo> findByPriorityAndUser(@Param("createdBy") String createdBy, @Param("priority") String priority);
    
    @Query("SELECT t FROM Todo t WHERE t.createdBy = :createdBy AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Todo> findByDateRange(@Param("createdBy") String createdBy, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.createdBy = :createdBy AND t.completed = true")
    Long countCompletedTodos(@Param("createdBy") String createdBy);
    
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.createdBy = :createdBy AND t.completed = false")
    Long countPendingTodos(@Param("createdBy") String createdBy);
} 