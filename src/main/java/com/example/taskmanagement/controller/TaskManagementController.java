package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.*;
import com.example.taskmanagement.repository.*;
import org.springframework.web.bind.annotation.*;
import com.example.taskmanagement.security.JwtUtil;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TaskManagementController {

    private final EmployeeRepository employeeRepo;
    private final TeamRepository teamRepo;
    private final TaskRepository taskRepo;
    private final JwtUtil jwtUtil;

    public TaskManagementController(EmployeeRepository employeeRepo,
                                    TeamRepository teamRepo,
                                    TaskRepository taskRepo,
                                    JwtUtil jwtUtil) {
        this.employeeRepo = employeeRepo;
        this.teamRepo = teamRepo;
        this.taskRepo = taskRepo;
        this.jwtUtil = jwtUtil;
    }

    // Employee CRUD
    @PostMapping("/employees")
    public EmployeeInfo addEmployee(@RequestBody EmployeeInfo employee) {
        return employeeRepo.save(employee);
    }

    @GetMapping("/employees")
    public List<EmployeeInfo> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @GetMapping("/employees/{id}")
    public EmployeeInfo getEmployeeById(@PathVariable Long id) {
        return employeeRepo.findById(id).orElse(null);
    }

    @PutMapping("/employees/{id}")
    public EmployeeInfo updateEmployee(@PathVariable Long id, @RequestBody EmployeeInfo employee) {
        employee.setEmployeeId(id);
        return employeeRepo.save(employee);
    }

    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepo.deleteById(id);
        return "Employee deleted successfully";
    }

    // Team CRUD
    @PostMapping("/teams")
    public TeamInfo addTeam(@RequestBody TeamInfo team) {
        return teamRepo.save(team);
    }

    @GetMapping("/teams")
    public List<TeamInfo> getAllTeams() {
        return teamRepo.findAll();
    }

    @GetMapping("/teams/{id}")
    public TeamInfo getTeamById(@PathVariable Long id) {
        return teamRepo.findById(id).orElse(null);
    }

    @PutMapping("/teams/{id}")
    public TeamInfo updateTeam(@PathVariable Long id, @RequestBody TeamInfo team) {
        team.setId(id);
        return teamRepo.save(team);
    }

    @DeleteMapping("/teams/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamRepo.deleteById(id);
        return "Team deleted successfully";
    }

    // Task CRUD
    @PostMapping("/tasks")
    public TaskInfo addTask(@RequestBody TaskInfo task) {
        task.setStatus(false);
        return taskRepo.save(task);
    }

    @GetMapping("/tasks")
    public List<TaskInfo> getAllTasks() {
        return taskRepo.findAll();
    }

    @GetMapping("/tasks/{id}")
    public TaskInfo getTaskById(@PathVariable Long id) {
        return taskRepo.findById(id).orElse(null);
    }

    @PutMapping("/tasks/{id}")
    public TaskInfo updateTask(@PathVariable Long id, @RequestBody TaskInfo task) {
        task.setId(id);
        return taskRepo.save(task);
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepo.deleteById(id);
        return "Task deleted successfully";
    }
    @GetMapping("/health")
    public String health() {
        return "Backend is awake";
    }

    // Login API
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody EmployeeInfo loginData) {
        Map<String, Object> response = new HashMap<>();

        Optional<EmployeeInfo> employeeOpt = employeeRepo.findById(loginData.getEmployeeId());

        if (employeeOpt.isEmpty()) {
            response.put("message", "Invalid employee ID");
            return response;
        }

        EmployeeInfo employee = employeeOpt.get();

        if (!employee.getPassword().equals(loginData.getPassword())) {
            response.put("message", "Invalid password");
            return response;
        }

        boolean isTeamLead = teamRepo.findAll()
                .stream()
                .anyMatch(team -> team.getTeamLead() != null &&
                        team.getTeamLead().getEmployeeId().equals(employee.getEmployeeId()));

        String token = jwtUtil.generateToken(employee.getEmployeeId());

        response.put("message", "Login successful");
        response.put("employee", employee);
        response.put("isTeamLead", isTeamLead);
        response.put("token", token);

        return response;
    }

    // Fetch task list by employee ID
    @GetMapping("/tasks/employee/{employeeId}")
    public List<TaskInfo> getTasksByEmployeeId(@PathVariable Long employeeId) {
        return taskRepo.findByAssigneeEmployeeId(employeeId);
    }

    // Fetch task list for team
    @GetMapping("/tasks/team/{teamId}")
    public List<TaskInfo> getTasksByTeamId(@PathVariable Long teamId) {
        List<EmployeeInfo> employees = employeeRepo.findByTeamId(teamId);
        List<TaskInfo> tasks = new ArrayList<>();

        for (EmployeeInfo employee : employees) {
            tasks.addAll(taskRepo.findByAssigneeEmployeeId(employee.getEmployeeId()));
        }

        return tasks;
    }

    // Fetch employee list with task list by team name in map structure
    @GetMapping("/team/{teamName}/employee-task-map")
    public Map<Long, List<TaskInfo>> getEmployeeTaskMapByTeamName(@PathVariable String teamName) {
        Map<Long, List<TaskInfo>> map = new HashMap<>();

        Optional<TeamInfo> teamOpt = teamRepo.findByName(teamName);

        if (teamOpt.isEmpty()) {
            return map;
        }

        TeamInfo team = teamOpt.get();
        List<EmployeeInfo> employees = employeeRepo.findByTeamId(team.getId());

        for (EmployeeInfo employee : employees) {
            map.put(employee.getEmployeeId(),
                    taskRepo.findByAssigneeEmployeeId(employee.getEmployeeId()));
        }

        return map;
    }

    // Mark task as completed
    @PutMapping("/tasks/{taskId}/complete")
    public TaskInfo completeTask(@PathVariable Long taskId) {
        TaskInfo task = taskRepo.findById(taskId).orElse(null);

        if (task != null) {
            task.setStatus(true);
            return taskRepo.save(task);
        }

        return null;
    }

    // Employees by team ID for dropdown
    @GetMapping("/employees/team/{teamId}")
    public List<EmployeeInfo> getEmployeesByTeamId(@PathVariable Long teamId) {
        return employeeRepo.findByTeamId(teamId);
    }
}