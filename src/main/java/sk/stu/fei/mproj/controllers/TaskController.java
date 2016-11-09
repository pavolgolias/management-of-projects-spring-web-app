package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.dto.project.UpdateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.task.CreateTaskRequestDto;
import sk.stu.fei.mproj.domain.dto.task.TaskDto;
import sk.stu.fei.mproj.domain.dto.task.UpdateTaskRequestDto;
import sk.stu.fei.mproj.domain.entities.Task;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.TaskService;

import javax.validation.Valid;

/**
 * Created by Patrik on 7.11.2016.
 */
@RestController
@Transactional
@RequestMapping("/api/task")
public class TaskController {

    private Mapper mapper;
    private TaskService taskService;

    @Autowired
    public TaskController(Mapper mapper, TaskService taskService){
        this.mapper=mapper;
        this.taskService=taskService;
    }

    @ApiOperation(value = "Get the task information of the current id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<TaskDto> getProject(@PathVariable Long taskId) {
        return new DataResponse<>(mapper.toTaskDto(taskService.getTask(taskId)));
    }

    @ApiOperation(value = "Create a task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unable to process data")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RoleSecured
    public DataResponse<TaskDto> createTask(@RequestBody @Valid CreateTaskRequestDto createTaskRequestDto){
        Task task = taskService.createTask(createTaskRequestDto);
        return new DataResponse<>(mapper.toTaskDto(task));
    }

    @ApiOperation(value = "Update information about specified task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable Entity")
    })
    @RequestMapping(value = "/{taskId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<TaskDto> updateProject(@PathVariable Long taskId, @RequestBody @Valid UpdateTaskRequestDto dto) {
        return new DataResponse<>(mapper.toTaskDto(taskService.updateTask(taskId, dto)));
    }

    @ApiOperation(value = "Delete specified task")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> deleteTask(@PathVariable Long taskId){
        taskService.deleteTask(taskId);
        return new DataResponse<>();
    }
}
