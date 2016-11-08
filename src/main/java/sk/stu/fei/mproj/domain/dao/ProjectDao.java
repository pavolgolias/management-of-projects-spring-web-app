package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Project;

import java.util.Date;

import static sk.stu.fei.mproj.domain.entities.QProject.project;

@Repository
@Transactional
public class ProjectDao extends DaoBase<Project, Long> {

    public Project findByName(String name) {
        return queryFactory.selectFrom(project)
                .where(project.name.eq(name))
                .fetchOne();
    }

    @Override
    public void persist(Project entity) {
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
    public void delete(Project entity) {
        entity.setDeletedAt(new Date());
        super.persist(entity);
    }

}
