package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.TaskInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskInfo, Long> {
    List<TaskInfo> findByAssigneeEmployeeId(Long employeeId);
}