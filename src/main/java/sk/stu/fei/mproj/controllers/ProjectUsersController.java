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
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.AccountService;
import sk.stu.fei.mproj.services.ProjectService;

/**
 * Created by Martin on 6.11.2016.
 */
@RestController
@Transactional
@RequestMapping("/api/projects")
public class ProjectUsersController {

    private final ProjectService projectService;
    private final AccountService accountService;
    private final Mapper mapper;

    @Autowired
    public ProjectUsersController(ProjectService projectService, AccountService accountService, Mapper mapper) {
        this.projectService = projectService;
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @ApiOperation(value = "Add administrator")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/administrators/{accountId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RoleSecured
    public DataResponse<ProjectDto> addAdministrator(@PathVariable Long projectId, @PathVariable Long accountId) {
        Project project = projectService.addAdministrator(projectId,accountService.getAccount(accountId));
        return new DataResponse<>(mapper.toProjectDto(project));
    }

    @ApiOperation(value = "Add participant")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/participants/{accountId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RoleSecured
    public DataResponse<ProjectDto> addParticipant(@PathVariable Long projectId, @PathVariable Long accountId) {
        Project project = projectService.addParticipant(projectId,accountService.getAccount(accountId));
        return new DataResponse<>(mapper.toProjectDto(project));
    }

    @ApiOperation(value = "Remove administrator")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{projectId}/administrators/{accountId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> removeAdministrator(@PathVariable Long projectId, @PathVariable Long accountId) {
        projectService.removeAdministrator(projectId,accountService.getAccount(accountId));
        return new DataResponse<>();
    }

    @ApiOperation(value = "Remove participant")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{projectId}/participants/{accountId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> removeParticipant(@PathVariable Long projectId, @PathVariable Long accountId) {
        projectService.removeParticipant(projectId,accountService.getAccount(accountId));
        return new DataResponse<>();
    }
}
