package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Task;

import java.util.Date;

/**
 * Created by Patrik on 7.11.2016.
 */
@Repository
@Transactional
public class TaskDao  extends DaoBase<Task, Long> {

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
}
