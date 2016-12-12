package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.entities.Task;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sk.stu.fei.mproj.domain.entities.QTask.task;

@Repository
@Transactional
public class TaskDao extends DaoBase<Task, Long> {

    @Override
    public void persist(Task entity) {
        Date now = new Date();
        if ( entity.getCreatedAt() == null ) {
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
        }
        else {
            entity.setUpdatedAt(now);
        }
        super.persist(entity);
    }

    @Override
    public void delete(Task entity) {
        entity.setDeletedAt(new Date());
    }

    public DataPage<List<Task>> findTasksByProjectPage(
            @NotNull Project project,
            @NotNull Long pageSize,
            @NotNull Long startId) {
        Task startTask;
        if ( startId <= 0 ) {
            startTask = queryFactory.selectFrom(task)
                    .where(task.project.eq(project))
                    .orderBy(task.createdAt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            startTask = queryFactory.selectFrom(task)
                    .where(task.project.eq(project)
                            .and(task.taskId.eq(startId)))
                    .fetchOne();
            if ( startTask == null ) {
                throw new EntityNotFoundException(String.format("Task id=%d not found", startId));
            }
        }

        if ( startTask == null ) {
            List<Task> data = new ArrayList<>();
            return new DataPage<>(data, 0L, -1L);
        }

        List<Task> data = queryFactory.selectFrom(task)
                .where(task.project.eq(project)
                        .and(task.deletedAt.isNull())
                        .andAnyOf(task.createdAt.before(startTask.getCreatedAt()),
                                task.createdAt.eq(startTask.getCreatedAt())))
                .orderBy(task.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();

        Long nextTaskId;
        if ( data.isEmpty() || data.size() < pageSize + 1 ) {
            nextTaskId = -1L;
        }
        else {
            nextTaskId = data.remove(data.size() - 1).getTaskId();
        }

        return new DataPage<>(data, (long) data.size(), nextTaskId);
    }

    public DataPage<List<Task>> findTasksAssignedForAccountPage(
            @NotNull Account account,
            @NotNull Long pageSize,
            @NotNull Long startId) {
        Task startTask;
        if ( startId <= 0 ) {
            startTask = queryFactory.selectFrom(task)
                    .where(task.assignee.eq(account))
                    .orderBy(task.createdAt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            startTask = queryFactory.selectFrom(task)
                    .where(task.assignee.eq(account)
                            .and(task.taskId.eq(startId)))
                    .fetchOne();
            if ( startTask == null ) {
                throw new EntityNotFoundException(String.format("Task id=%d not found", startId));
            }
        }

        if ( startTask == null ) {
            List<Task> data = new ArrayList<>();
            return new DataPage<>(data, 0L, -1L);
        }

        List<Task> data = queryFactory.selectFrom(task)
                .where(task.assignee.eq(account)
                        .and(task.deletedAt.isNull())
                        .andAnyOf(task.createdAt.before(startTask.getCreatedAt()),
                                task.createdAt.eq(startTask.getCreatedAt())))
                .orderBy(task.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();

        Long nextTaskId;
        if ( data.isEmpty() || data.size() < pageSize + 1 ) {
            nextTaskId = -1L;
        }
        else {
            nextTaskId = data.remove(data.size() - 1).getTaskId();
        }

        return new DataPage<>(data, (long) data.size(), nextTaskId);
    }
}
