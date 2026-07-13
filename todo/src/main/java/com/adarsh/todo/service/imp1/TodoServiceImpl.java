package com.adarsh.todo.service.imp1;

import com.adarsh.todo.dto.TodoDTO;
import com.adarsh.todo.dto.TodoHistoryDTO;
import com.adarsh.todo.dto.TodoStatsDTO;
import com.adarsh.todo.entity.Action;
import com.adarsh.todo.entity.Category;
import com.adarsh.todo.entity.Priority;
import com.adarsh.todo.entity.Todo;
import com.adarsh.todo.entity.TodoHistory;
import com.adarsh.todo.repository.TodoHistoryRepository;
import com.adarsh.todo.repository.TodoRepository;
import com.adarsh.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoServiceImpl implements TodoService {
    
    private final TodoRepository todoRepository;
    private final TodoHistoryRepository historyRepository;
    
    @Override
    public TodoDTO createTodo(TodoDTO todoDTO, String username) {
        log.info("Creating todo for user: {}", username);
        
        Todo todo = new Todo();
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setPriority(Priority.valueOf(todoDTO.getPriority()));
        todo.setCategory(Category.valueOf(todoDTO.getCategory()));
        todo.setCreatedBy(username);
        
        Todo saved = todoRepository.save(todo);
        saveHistory(saved.getId(), Action.CREATED, null, saved.getTitle(), username);
        
        log.info("Todo created with id: {}", saved.getId());
        return mapToDTO(saved);
    }
    
    @Override
    public TodoDTO updateTodo(Long id, TodoDTO todoDTO, String username) {
        log.info("Updating todo id: {} by user: {}", id, username);
        
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Todo not found"));
        
        String oldTitle = todo.getTitle();
        
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setPriority(Priority.valueOf(todoDTO.getPriority()));
        todo.setCategory(Category.valueOf(todoDTO.getCategory()));
        
        Todo updated = todoRepository.save(todo);
        
        if (!oldTitle.equals(updated.getTitle())) {
            saveHistory(id, Action.UPDATED, oldTitle, updated.getTitle(), username);
        }
        
        return mapToDTO(updated);
    }
    
    @Override
    public TodoDTO toggleTodo(Long id, String username) {
        log.info("Toggling todo id: {} by user: {}", id, username);
        
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Todo not found"));
        
        boolean wasCompleted = todo.getCompleted();
        todo.setCompleted(!wasCompleted);
        
        Todo updated = todoRepository.save(todo);
        
        Action action = !wasCompleted ? Action.COMPLETED : Action.INCOMPLETE;
        saveHistory(id, action, String.valueOf(wasCompleted), String.valueOf(!wasCompleted), username);
        
        return mapToDTO(updated);
    }
    
    @Override
    public void deleteTodo(Long id, String username) {
        log.info("Deleting todo id: {} by user: {}", id, username);
        
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Todo not found"));
        
        saveHistory(id, Action.DELETED, todo.getTitle(), null, username);
        todoRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TodoDTO getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Todo not found"));
        return mapToDTO(todo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TodoDTO> getUserTodos(String username, Pageable pageable) {
        return todoRepository.findByCreatedBy(username, pageable)
            .map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TodoDTO> getFilteredTodos(String username, Boolean completed, Pageable pageable) {
        if (completed != null) {
            return todoRepository.findByCompletedAndCreatedBy(completed, username, pageable)
                .map(this::mapToDTO);
        }
        return getUserTodos(username, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TodoDTO> getOverdueTodos(String username) {
        LocalDateTime now = LocalDateTime.now();
        return todoRepository.findByDateRange(username, 
                LocalDateTime.of(1900, 1, 1, 0, 0), now)
            .stream()
            .filter(t -> !t.getCompleted())
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TodoStatsDTO getStats(String username) {
        List<Todo> allTodos = todoRepository.findByCreatedBy(username);
        Long total = (long) allTodos.size();
        Long completed = todoRepository.countCompletedTodos(username);
        Long pending = todoRepository.countPendingTodos(username);
        
        Double percentage = total > 0 ? (completed.doubleValue() / total) * 100 : 0;
        Long overdue = (long) getOverdueTodos(username).size();
        
        return new TodoStatsDTO(total, completed, pending, percentage, overdue);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TodoHistoryDTO> getTodoHistory(Long todoId) {
        return historyRepository.findByTodoIdOrderByChangedAtDesc(todoId)
            .stream()
            .map(this::mapHistoryToDTO)
            .collect(Collectors.toList());
    }
    
    private void saveHistory(Long todoId, Action action, String oldValue, String newValue, String username) {
        TodoHistory history = new TodoHistory();
        history.setTodoId(todoId);
        history.setAction(action);
        history.setPreviousValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(username);
        
        historyRepository.save(history);
        log.info("History saved for todo {}: {}", todoId, action);
    }
    
    private TodoDTO mapToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.getCompleted());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        dto.setDueDate(todo.getDueDate());
        dto.setPriority(todo.getPriority().toString());
        dto.setCategory(todo.getCategory().toString());
        dto.setCreatedBy(todo.getCreatedBy());
        
        if (todo.getCompleted() && todo.getUpdatedAt() != null) {
            long days = ChronoUnit.DAYS.between(todo.getCreatedAt(), todo.getUpdatedAt());
            dto.setCompletedInDays(days);
        }
        
        return dto;
    }
    
    private TodoHistoryDTO mapHistoryToDTO(TodoHistory history) {
        return new TodoHistoryDTO(
            history.getId(),
            history.getTodoId(),
            history.getAction().toString(),
            history.getPreviousValue(),
            history.getNewValue(),
            history.getChangedAt(),
            history.getChangedBy()
        );
    }
}