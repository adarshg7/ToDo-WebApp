package com.adarsh.todo.service;

import com.adarsh.todo.dto.TodoDTO;
import com.adarsh.todo.dto.TodoHistoryDTO;
import com.adarsh.todo.dto.TodoStatsDTO;;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoService {
	TodoDTO createTodo(TodoDTO todoDTO, String username);
	
	TodoDTO updateTodo(Long id, TodoDTO todoDTO, String username);
	
	TodoDTO toggleTodo(Long id, String username);
	
	void deleteTodo(Long id, String username);
	
	TodoDTO getTodoById(Long id);
	
	Page<TodoDTO> getUserTodos(String username, Pageable pageable);
    
    Page<TodoDTO> getFilteredTodos(String username, Boolean completed, Pageable pageable);
    
    List<TodoDTO> getOverdueTodos(String username);
    
    TodoStatsDTO getStats(String username);
    
    List<TodoHistoryDTO> getTodoHistory(Long todoId);
	
}
