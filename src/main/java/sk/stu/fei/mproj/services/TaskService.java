package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dao.AccountDao;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dao.ProjectDao;
import sk.stu.fei.mproj.domain.dao.TaskDao;
import sk.stu.fei.mproj.domain.dto.task.CreateTaskRequestDto;
import sk.stu.fei.mproj.domain.dto.task.UpdateTaskRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.entities.Task;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskStatus;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@Transactional
public class TaskService {
    private TaskDao taskDao;
    private Mapper mapper;
    private AuthorizationManager authorizationManager;
    private AccountDao accountDao;
    private ProjectDao projectDao;

    @Autowired
    public TaskService(TaskDao taskDao, Mapper mapper, AuthorizationManager authorizationManager, AccountDao accountDao, ProjectDao projectDao) {
        this.taskDao = taskDao;
        this.mapper = mapper;
        this.authorizationManager = authorizationManager;
        this.accountDao = accountDao;
        this.projectDao = projectDao;
    }

    @RoleSecured
    public Task getTask(Long projectId, Long taskId) {
        return getOrElseThrowEntityNotFoundEx(taskId, taskDao, String.format("Task id=%d not found", taskId));
    }

    @RoleSecured
    public Task createTask(Long projectId, @NotNull CreateTaskRequestDto dto) {
        Objects.requireNonNull(dto);

        final Project project = getOrElseThrowEntityNotFoundEx(projectId, projectDao, String.format("Project id=%d not found", projectId));
        checkAddTaskEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to add task to project id=%d", projectId)
        );

        final Task task = mapper.toTask(dto);
        task.setStatus(TaskStatus.Todo);
        if ( task.getPriority() == null ) {
            task.setPriority(TaskPriority.Normal);
        }
        task.setProject(project);
        task.setAuthor(authorizationManager.getCurrentAccount());
        if ( dto.getAssigneeId() != null ) {
            final Account assignee = getOrElseThrowEntityNotFoundEx(
                    dto.getAssigneeId(),
                    accountDao,
                    String.format("User with id=%s was not found", dto.getAssigneeId()));
            task.setAssignee(assignee);
        }

        taskDao.persist(task);
        return task;
    }

    @RoleSecured
    public Task updateTask(Long projectId, Long taskId, @NotNull UpdateTaskRequestDto dto) {
        Objects.requireNonNull(dto);

        final Task task = getTask(projectId, taskId);
        checkUpdateTaskEligibilityOrElseThrowSecurityEx(
                task,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update or delete task id=%d", taskId)
        );

        mapper.fillTask(dto, task);
        if ( dto.getAssigneeId() != null ) {
            final Account assignee = getOrElseThrowEntityNotFoundEx(
                    dto.getAssigneeId(),
                    accountDao,
                    String.format("User with id=%s was not found", dto.getAssigneeId()));
            task.setAssignee(assignee);
        }
        else {
            task.setAssignee(null);
        }

        taskDao.persist(task);
        return task;
    }

    @RoleSecured
    public void deleteTask(Long projectId, Long taskId) {
        final Task task = getTask(projectId, taskId);

        checkUpdateTaskEligibilityOrElseThrowSecurityEx(
                task,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update or delete task id=%d", taskId)
        );

        taskDao.delete(task);
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    private void checkAddTaskEligibilityOrElseThrowSecurityEx(Project projectToAddTaskTo, Account who, String exceptionMessage) {
        if ( !projectToAddTaskTo.getAdministrators().contains(who) ) {
            throw new SecurityException(exceptionMessage);
        }
    }

    private void checkUpdateTaskEligibilityOrElseThrowSecurityEx(Task updateTarget, Account who, String exceptionMessage) {
        if ( !updateTarget.getProject().getAdministrators().contains(who) ) {
            throw new SecurityException(exceptionMessage);
        }
    }
}
