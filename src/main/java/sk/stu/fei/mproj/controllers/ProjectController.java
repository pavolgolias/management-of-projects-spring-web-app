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
import sk.stu.fei.mproj.domain.dto.project.CreateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.dto.project.UpdateProjectRequestDto;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.ProjectService;

import javax.validation.Valid;

/**
 * Created by Martin on 31.10.2016.
 */
@RestController
@Transactional
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final Mapper mapper;
    private final AuthorizationManager authorizationManager;

    @Autowired
    public ProjectController(ProjectService projectService, Mapper mapper, AuthorizationManager authorizationManager) {
        this.projectService = projectService;
        this.mapper = mapper;
        this.authorizationManager = authorizationManager;
    }

    @ApiOperation(value = "Create project")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<ProjectDto> createProject(@RequestBody @Valid CreateProjectRequestDto dto) {
        Project project = projectService.createProject(dto);
        return new DataResponse<>(mapper.toProjectDto(project));
    }

    @ApiOperation(value = "Update information about specified project")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ProjectDto> updateProject(@PathVariable Long projectId, @RequestBody @Valid UpdateProjectRequestDto dto) {
        return new DataResponse<>(mapper.toProjectDto(projectService.updateProject(projectId, dto)));
    }

    @ApiOperation(value = "Delete specified project")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return new DataResponse<>();
    }
}
