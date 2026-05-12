package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeInfo, Long> {
    List<EmployeeInfo> findByTeamId(Long teamId);
}