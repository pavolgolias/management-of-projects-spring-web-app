package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.dto.task.TaskDto;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.TaskService;

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
}
