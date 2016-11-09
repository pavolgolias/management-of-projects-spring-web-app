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
import sk.stu.fei.mproj.domain.dao.DataPage;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.PageableDataResponse;
import sk.stu.fei.mproj.domain.dto.project.CreateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.dto.project.UpdateProjectRequestDto;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.enums.GetProjectsType;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.ProjectService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final Mapper mapper;

    @Autowired
    public ProjectController(ProjectService projectService, Mapper mapper) {
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @ApiOperation(value = "Create project")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RoleSecured
    public DataResponse<ProjectDto> createProject(@RequestBody @Valid CreateProjectRequestDto dto) {
        Project project = projectService.createProject(dto);
        return new DataResponse<>(mapper.toProjectDto(project));
    }

    @ApiOperation(value = "Get specified project information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ProjectDto> getProject(@PathVariable Long projectId) {
        return new DataResponse<>(mapper.toProjectDto(projectService.getProject(projectId)));
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

    @ApiOperation(value = "Get specified page of projects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public PageableDataResponse<List<ProjectDto>> getProjects(
            @RequestParam(defaultValue = "All", required = false) GetProjectsType type,
            @RequestParam(defaultValue = "-1", required = false) Long nextId,
            @RequestParam(defaultValue = "20", required = false) Long pageSize) {
        DataPage<List<Project>> projectsPage = projectService.getProjectsPage(type, pageSize, nextId);
        return new PageableDataResponse<>(mapper.toProjectDtoList(projectsPage.getPage()), projectsPage.getPageSize(), projectsPage.getNextId());
    }
}
