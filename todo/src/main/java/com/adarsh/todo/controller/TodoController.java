package com.adarsh.todo.controller;

import com.adarsh.todo.dto.TodoDTO;
import com.adarsh.todo.dto.TodoHistoryDTO;
import com.adarsh.todo.dto.TodoStatsDTO;
import com.adarsh.todo.service.TodoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@AllArgsConstructor
@Slf4j
public class TodoController {
    
    private final TodoService todoService;
    
    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@RequestBody TodoDTO todoDTO,
                                              @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Creating todo for user: {}", username);
        TodoDTO created = todoService.createTodo(todoDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        log.info("Fetching todo with id: {}", id);
        TodoDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }
    
    @GetMapping
    public ResponseEntity<Page<TodoDTO>> getUserTodos(
            @RequestHeader(value = "X-Username", defaultValue = "default-user") String username,
            Pageable pageable) {
        log.info("Fetching todos for user: {}", username);
        Page<TodoDTO> todos = todoService.getUserTodos(username, pageable);
        return ResponseEntity.ok(todos);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<TodoDTO>> getFilteredTodos(
            @RequestHeader(value = "X-Username", defaultValue = "default-user") String username,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        log.info("Fetching filtered todos for user: {} with completed status: {}", username, completed);
        Page<TodoDTO> todos = todoService.getFilteredTodos(username, completed, pageable);
        return ResponseEntity.ok(todos);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<TodoDTO>> getOverdueTodos(
            @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Fetching overdue todos for user: {}", username);
        List<TodoDTO> todos = todoService.getOverdueTodos(username);
        return ResponseEntity.ok(todos);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<TodoStatsDTO> getStats(
            @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Fetching stats for user: {}", username);
        TodoStatsDTO stats = todoService.getStats(username);
        return ResponseEntity.ok(stats);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id,
                                              @RequestBody TodoDTO todoDTO,
                                              @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Updating todo id: {} for user: {}", id, username);
        TodoDTO updated = todoService.updateTodo(id, todoDTO, username);
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDTO> toggleTodo(@PathVariable Long id,
                                              @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Toggling todo id: {} for user: {}", id, username);
        TodoDTO toggled = todoService.toggleTodo(id, username);
        return ResponseEntity.ok(toggled);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id,
                                           @RequestHeader(value = "X-Username", defaultValue = "default-user") String username) {
        log.info("Deleting todo id: {} for user: {}", id, username);
        todoService.deleteTodo(id, username);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TodoHistoryDTO>> getTodoHistory(@PathVariable Long id) {
        log.info("Fetching history for todo id: {}", id);
        List<TodoHistoryDTO> history = todoService.getTodoHistory(id);
        return ResponseEntity.ok(history);
    }
    
}
