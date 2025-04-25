package com.binarybrain.task.controller;

import com.binarybrain.exception.ErrorDetails;
import com.binarybrain.task.dto.TaskDto;
import com.binarybrain.task.model.Task;
import com.binarybrain.task.model.TaskStatus;
import com.binarybrain.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Create Assignment",
            tags = {"01 - Creation"},
            description = "Only TEACHER can create assignment in a course.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to create course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto,
                                              @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        TaskDto createdTask = taskService.createTask(taskDto, username);

        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Search a Task by id",
            tags = {"02 - Search Task"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found with given id!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id,
                                               @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        TaskDto taskDto = taskService.getTaskById(id,username);

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Search all tasks, optionally filtered by status",
            tags = {"02 - Search Task"},
            description = "Returns list of all tasks. If `TaskStatus` is given then return task list filtered by status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks retrieved Successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "Only ADMIN have the permission to search all Courses.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTask(@RequestParam(required = false) TaskStatus status,
                                                    @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getAllTask(status, username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @Operation(
            summary = "Search all tasks  by Teacher id",
            tags = {"02 - Search Task"},
            description = "Returns a list of task created by a specific teacher. One Teacher can't search for another teacher tasks!",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to search another teacher's tasks!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Teacher not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TaskDto>> getAllTaskByTeacherId(@PathVariable Long teacherId,
                                                               @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getAllTaskByTeacherId(teacherId,username);

        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @Operation(
            summary = "Search Tasks with list of taskId",
            tags = {"02 - Search Task"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/by-ids")
    public ResponseEntity<List<TaskDto>> getTasksByIds(@RequestParam List<Long> taskIds,
                                                       @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        List<TaskDto> taskDtoList = taskService.getTasksbyIds(taskIds, username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @Operation(
            summary = "Close Task",
            description = "TaskStatus will be changed to `CLOSED`. Only Admin & corresponding Course Teacher can do this.",
            tags = {"03 - Manage Task"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/close/{id}")
    public ResponseEntity<TaskDto> closeTask(@PathVariable Long id,
                                             @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        TaskDto taskDto = taskService.closeTask(id, username);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Modify a existing task",
            tags = {"03 - Manage Task"},
            description = "Admin or Course Teacher can upgrade task information e.g. Change TaskStatus from `OPEN` to `CLOSED` or `DONE` and vice versa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task modification successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to modify assignment which is created by another teacher'!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id,
                                              @RequestBody TaskDto taskDto,
                                              @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        TaskDto updatedTaskDto = taskService.updateTask(id, taskDto, username);
        return new ResponseEntity<>(updatedTaskDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete assignment",
            tags = {"04 - Delete Task"},
            description = "Admin or corresponding Teacher can delete assignment.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted task."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to delete another teacher's course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id,
                                               @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        taskService.deleteTaskById(id,username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
