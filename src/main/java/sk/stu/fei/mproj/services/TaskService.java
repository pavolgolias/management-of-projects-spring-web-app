package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dao.TaskDao;
import sk.stu.fei.mproj.domain.entities.Task;
import sk.stu.fei.mproj.security.RoleSecured;

import javax.persistence.EntityNotFoundException;

/**
 * Created by Patrik on 7.11.2016.
 */
@Service
@Transactional
public class TaskService {

    private TaskDao taskDao;

    @Autowired
    public TaskService(TaskDao taskDao){
        this.taskDao=taskDao;
    }

    @RoleSecured
    public Task getTask(Long taskId){
        return getOrElseThrowEntityNotFoundEx(taskId, taskDao, String.format("Task id=%d not found", taskId));
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }



}
