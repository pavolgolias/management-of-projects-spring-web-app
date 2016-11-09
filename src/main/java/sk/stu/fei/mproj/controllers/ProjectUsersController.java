package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.enums.ModifyProjectUserAction;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.ProjectService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/projects")
public class ProjectUsersController {

    private final ProjectService projectService;
    private final Mapper mapper;

    @Autowired
    public ProjectUsersController(ProjectService projectService, Mapper mapper) {
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @ApiOperation(value = "Modify project administrators")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/administrators", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ProjectDto> modifyAdministrators(@PathVariable Long projectId, @RequestParam ModifyProjectUserAction action, @RequestBody @Valid List<Long> administratorAccountIds) {
        switch ( action ) {
            case Add:
                return new DataResponse<>(mapper.toProjectDto(projectService.addAdministrators(projectId, administratorAccountIds)));
            case Remove:
                return new DataResponse<>(mapper.toProjectDto(projectService.removeAdministrators(projectId, administratorAccountIds)));
            default:
                throw new IllegalArgumentException("Wrong action parameter");
        }
    }

    @ApiOperation(value = "Search for users that can be added as project administrators")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/administrators/suggest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<AccountDto>> suggestAdministrators(@PathVariable Long projectId, @RequestParam String searchKey,
                                                                @RequestParam(defaultValue = "10", required = false) Long limit) {
        return new DataResponse<>(mapper.toAccountDtoList(projectService.suggestAdministratorsToAdd(projectId, searchKey, limit)));
    }

    @ApiOperation(value = "Modify project participants")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/participants", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ProjectDto> modifyParticipants(@PathVariable Long projectId, @RequestParam ModifyProjectUserAction action, @RequestBody List<Long> participantsAccountIds) {
        switch ( action ) {
            case Add:
                return new DataResponse<>(mapper.toProjectDto(projectService.addParticipants(projectId, participantsAccountIds)));
            case Remove:
                return new DataResponse<>(mapper.toProjectDto(projectService.removeParticipants(projectId, participantsAccountIds)));
            default:
                throw new IllegalArgumentException("Wrong action parameter");
        }
    }

    @ApiOperation(value = "Search for users that can be added as project participants")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{projectId}/participants/suggest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<AccountDto>> suggestParticipants(@PathVariable Long projectId, @RequestParam String searchKey,
                                                              @RequestParam(defaultValue = "10", required = false) Long limit) {
        return new DataResponse<>(mapper.toAccountDtoList(projectService.suggestParticipantsToAdd(projectId, searchKey, limit)));
    }
}
