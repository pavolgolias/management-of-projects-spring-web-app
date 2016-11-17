package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.task.CreateTaskRequestDto;
import sk.stu.fei.mproj.domain.dto.task.TaskDto;
import sk.stu.fei.mproj.domain.dto.task.UpdateTaskRequestDto;
import sk.stu.fei.mproj.domain.entities.Task;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.ProjectTaskService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/projects/{projectId}/tasks")
public class ProjectTaskController {
    private final Mapper mapper;
    private final ProjectTaskService projectTaskService;

    @Autowired
    public ProjectTaskController(Mapper mapper, ProjectTaskService projectTaskService) {
        this.mapper = mapper;
        this.projectTaskService = projectTaskService;
    }

    @ApiOperation(value = "Get information about specified task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<TaskDto> getTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        return new DataResponse<>(mapper.toTaskDto(projectTaskService.getTask(projectId, taskId)));
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
    public DataResponse<TaskDto> createTask(@PathVariable Long projectId, @RequestBody @Valid CreateTaskRequestDto dto) {
        Task task = projectTaskService.createTask(projectId, dto);
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
    public DataResponse<TaskDto> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody @Valid UpdateTaskRequestDto dto) {
        return new DataResponse<>(mapper.toTaskDto(projectTaskService.updateTask(projectId, taskId, dto)));
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
    public DataResponse<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        projectTaskService.deleteTask(projectId, taskId);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Get all tasks for project")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<TaskDto>> getAllTasks(@PathVariable Long projectId) {
        return new DataResponse<>(mapper.toTaskDtoList(projectTaskService.getAllTasks(projectId)));
    }
}
