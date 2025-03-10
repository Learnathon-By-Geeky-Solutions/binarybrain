package com.binaryBrain.task_microservice.controller;

import com.binaryBrain.task_microservice.dto.TaskDto;
import com.binaryBrain.task_microservice.model.TaskStatus;
import com.binaryBrain.task_microservice.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto,
                                                   @RequestHeader("X-User-Username") String username){


        TaskDto createdTask = taskService.createTask(taskDto, username);

        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id,
                                               @RequestHeader("X-User-Username") String username){
        TaskDto taskDto = taskService.getTaskById(id,username);

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTask(@RequestParam(required = false) TaskStatus status,
                                              @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getAllTask(status, username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TaskDto>> getAllTaskByTeacherId(@PathVariable Long teacherId,
                                               @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getAllTaskByTeacherId(teacherId,username);

        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<TaskDto>> getTasksByIds(@RequestParam List<Long> taskIds,
                                                       @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getTasksbyIds(taskIds, username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<TaskDto> closeTask(@PathVariable Long id,
                                             @RequestHeader("X-User-Username") String username){
        TaskDto taskDto = taskService.closeTask(id, username);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id,
                                              @RequestBody TaskDto taskDto,
                                              @RequestHeader("X-User-Username") String username){
        TaskDto updatedTaskDto = taskService.updateTask(id, taskDto, username);
        return new ResponseEntity<>(updatedTaskDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id,
                                               @RequestHeader("X-User-Username") String username){
        taskService.deleteTaskById(id,username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
