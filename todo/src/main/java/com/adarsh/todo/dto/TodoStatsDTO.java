package com.adarsh.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatsDTO {
	private Long totalTodos;
	private Long completedTodos;
	private Long pendingTodos;
	private Double completionPercentage;
	private Long overdueTodos;
	
	
}
