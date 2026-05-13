package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    private String taskTitle;

    private String taskDescription;

    private int noOfDays;

    private String priority;

    private boolean status = false;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeInfo assignee;
}