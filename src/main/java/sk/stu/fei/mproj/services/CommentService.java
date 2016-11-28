package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dao.CommentDao;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dao.DataPage;
import sk.stu.fei.mproj.domain.dao.TaskDao;
import sk.stu.fei.mproj.domain.dto.comment.CreateCommentRequestDto;
import sk.stu.fei.mproj.domain.dto.comment.UpdateCommentRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Comment;
import sk.stu.fei.mproj.domain.entities.Task;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class CommentService {

    private final CommentDao commentDao;
    private final Mapper mapper;
    private final AuthorizationManager authorizationManager;
    private final ProjectTaskService projectTaskService;
    private final TaskDao taskDao;

    @Autowired
    public CommentService(Mapper mapper, CommentDao commentDao, AuthorizationManager authorizationManager, ProjectTaskService projectTaskService, TaskDao taskDao) {
        this.mapper = mapper;
        this.commentDao = commentDao;
        this.authorizationManager = authorizationManager;
        this.projectTaskService = projectTaskService;
        this.taskDao = taskDao;
    }

    @RoleSecured
    public Comment createComment(Long taskId, @NotNull CreateCommentRequestDto dto) {
        Objects.requireNonNull(dto);

        Comment comment = mapper.toComment(dto);
        Account account = authorizationManager.getCurrentAccount();
        Task task = projectTaskService.getTask(taskId);

        comment.setAuthor(account);
        comment.setTask(task);

        commentDao.persist(comment);

        return comment;
    }

    @RoleSecured
    public Comment updateComment(Long commentId, @NotNull UpdateCommentRequestDto dto) {
        Objects.requireNonNull(dto);

        final Comment comment = getComment(commentId);
        checkUpdateCommentEligibilityOrElseThrowSecurityEx(
                comment,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update or delete comment id=%d", commentId)
        );

        mapper.fillComment(dto, comment);

        commentDao.persist(comment);
        return comment;
    }

    @RoleSecured
    public void deleteComment(Long commentId) {
        final Comment comment = getComment(commentId);

        checkUpdateCommentEligibilityOrElseThrowSecurityEx(
                comment,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update or delete comment id=%d", commentId)
        );

        commentDao.delete(comment);
    }

    @RoleSecured
    public Comment getComment(Long commentId) {
        return getOrElseThrowEntityNotFoundEx(commentId, commentDao, String.format("Comment id=%d not found", commentId));
    }

    @RoleSecured
    public DataPage<List<Comment>> getCommentPage(Long pageSize, Long startId, Long taskId) {
        final Task task = getOrElseThrowEntityNotFoundEx(taskId, taskDao, String.format("Task id=%d not found", taskId));
        return commentDao.findCommentPageFilteredByTask(pageSize, startId, task);
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    private void checkUpdateCommentEligibilityOrElseThrowSecurityEx(Comment updateTarget, Account who, String exceptionMessage) {
        if ( !updateTarget.getAuthor().equals(who) ) {
            throw new SecurityException(exceptionMessage);
        }
    }

}
