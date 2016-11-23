package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.entities.Task;

import javax.validation.constraints.NotNull;
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

    public List<Task> findAllTasksByProject(@NotNull Project project) {
        return queryFactory.selectFrom(task)
                .where(task.project.eq(project)
                        .and(task.deletedAt.isNull()))
                .fetch();
    }

}
