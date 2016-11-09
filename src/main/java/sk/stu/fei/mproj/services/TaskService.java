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
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Patrik on 7.11.2016.
 */
@Service
@Transactional
public class TaskService {

    private TaskDao taskDao;
    private Mapper mapper;
    private AuthorizationManager authorizationManager;
    private AccountDao accountDao;
    private ProjectDao projectDao;

    @Autowired
    public TaskService(TaskDao taskDao,Mapper mapper,AuthorizationManager authorizationManager,AccountDao accountDao,ProjectDao projectDao)
    {
        this.taskDao=taskDao;
        this.mapper = mapper;
        this.authorizationManager = authorizationManager;
        this.accountDao = accountDao;
        this.projectDao = projectDao;
    }

    @RoleSecured
    public Task getTask(Long taskId){
        return getOrElseThrowEntityNotFoundEx(taskId, taskDao, String.format("Task id=%d not found", taskId));
    }

    @RoleSecured
    public Task createTask(CreateTaskRequestDto createTaskRequestDto){
        Task task = mapper.toTask(createTaskRequestDto);
        task.setCreatedAt(new Date());
        task.setCreator(authorizationManager.getCurrentAccount());
        if(createTaskRequestDto.getAssignee() != null){
            Account assignee= accountDao.findById(createTaskRequestDto.getAssignee());
            if(assignee == null){
                throw new IllegalArgumentException(String.format("User with id=%s was not found",createTaskRequestDto.getAssignee()
                ));
            }else{
                task.setAssignee(assignee);
            }
        }
        task.setProject(projectDao.findById(createTaskRequestDto.getProject()));
        if(task.getProject()==null)
            throw new IllegalArgumentException("The task is not assigned to project.");

        taskDao.persist(task);
        return task;
    }

    public Task updateTask(Long id,UpdateTaskRequestDto updateTaskRequestDto){
        Objects.requireNonNull(updateTaskRequestDto);
        Task task = taskDao.findById(id);

        mapper.fillTask(updateTaskRequestDto,task);
        task.setUpdatedAt(new Date());
        if(updateTaskRequestDto.getAssignee() != null){
            Account assignee= accountDao.findById(updateTaskRequestDto.getAssignee());
            if(assignee == null){
                throw new IllegalArgumentException(String.format("User with id=%s was not found",updateTaskRequestDto.getAssignee()
                ));
            }else{
                task.setAssignee(assignee);
            }
        }else{
            task.setAssignee(null);
        }
        task.setProject(projectDao.findById(updateTaskRequestDto.getProject()));
        if(task.getProject()==null)
            throw new IllegalArgumentException("The task is not assigned to project.");

        taskDao.persist(task);
        return task;
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    public void deleteTask(Long id){
        Task task = taskDao.findById(id);
        if(task == null)
            throw new IllegalArgumentException(String.format("Task with id=%s was not found",id.toString()));

        checkUpdateEligibilityOrElseThrowSecurityEx(task,authorizationManager.getCurrentAccount(),"Permission denied. Only the owner of the task or project administrator can delete the task.");

        taskDao.delete(task);
    }

    private void checkUpdateEligibilityOrElseThrowSecurityEx(Task updateTarget, Account who, String exceptionMessage) {
        if ( !updateTarget.getCreator().equals(who) && !updateTarget.getAssignee().equals(who) && !updateTarget.getProject().getAdministrators().contains(who)) {
            throw new SecurityException(exceptionMessage);
        }
    }

}
