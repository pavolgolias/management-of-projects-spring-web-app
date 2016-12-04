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
import sk.stu.fei.mproj.domain.dto.comment.CommentDto;
import sk.stu.fei.mproj.domain.dto.comment.CreateCommentRequestDto;
import sk.stu.fei.mproj.domain.dto.comment.UpdateCommentRequestDto;
import sk.stu.fei.mproj.domain.entities.Comment;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    private final Mapper mapper;
    private final CommentService commentService;

    @Autowired
    public CommentController(Mapper mapper, CommentService commentService) {
        this.mapper = mapper;
        this.commentService = commentService;
    }

    @ApiOperation(value = "Create a comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 422, message = "Unable to process data")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RoleSecured
    public DataResponse<CommentDto> createComment(@PathVariable Long projectId,
                                                  @PathVariable Long taskId,
                                                  @RequestBody @Valid CreateCommentRequestDto dto) {
        Comment comment = commentService.createComment(taskId, dto);
        return new DataResponse<>(mapper.toCommentDto(comment));
    }

    @ApiOperation(value = "Update information about specified comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable Entity")
    })
    @RequestMapping(value = "/{commentId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<CommentDto> updateComment(@PathVariable Long projectId,
                                                  @PathVariable Long taskId,
                                                  @PathVariable Long commentId,
                                                  @RequestBody @Valid UpdateCommentRequestDto dto) {
        return new DataResponse<>(mapper.toCommentDto(commentService.updateComment(commentId, dto)));
    }

    @ApiOperation(value = "Delete specified comment")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{commentId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Get information about specified comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{commentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<CommentDto> getComment(@PathVariable Long projectId,
                                               @PathVariable Long taskId,
                                               @PathVariable Long commentId) {
        return new DataResponse<>(mapper.toCommentDto(commentService.getComment(commentId)));
    }

    @ApiOperation(value = "Get specified page of comments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public PageableDataResponse<List<CommentDto>> getComments(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "-1", required = false) Long nextId,
            @RequestParam(defaultValue = "20", required = false) Long pageSize) {
        DataPage<List<Comment>> commentPage = commentService.getCommentPage(pageSize, nextId, taskId);
        return new PageableDataResponse<>(mapper.toCommentDtoList(commentPage.getPage()), commentPage.getPageSize(), commentPage.getNextId());
    }
}
