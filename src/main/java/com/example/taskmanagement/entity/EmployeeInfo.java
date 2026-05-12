package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfo {

    @Id
    private Long employeeId;

    private String employeeName;

    private String password;

    private Long teamId;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TaskInfo> taskList;
}