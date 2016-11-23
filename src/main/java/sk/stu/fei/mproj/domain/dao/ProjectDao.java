package sk.stu.fei.mproj.domain.dao;

import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.enums.GetProjectsType;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * Method to construct query and get projects page. <br/>
     * It uses startId of the next project to load to prevent duplicates <br/>
     * when pages are loaded on client caused by project insertion <br/>
     * between two subsequent page requests.
     *
     * @param account  Account of user who is performing request
     * @param type     Type of requested projects
     * @param pageSize Max number of items returned by request
     * @param startId  Project id from where to start listing items <br />
     *                 or number less or equal to zero to request first page
     * @return DataPage instance with results
     */
    public DataPage<List<Project>> findProjectsPageFilteredByType(
            @NotNull Account account,
            @NotNull GetProjectsType type,
            @NotNull Long pageSize,
            @NotNull Long startId) {

        Project startProject;
        if ( startId <= 0 ) {
            startProject = queryFactory.selectFrom(project)
                    .where(project.deletedAt.isNull())
                    .orderBy(project.createdAt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            startProject = queryFactory.selectFrom(project)
                    .where(project.deletedAt.isNull()
                            .and(project.projectId.eq(startId)))
                    .fetchOne();
            if ( startProject == null ) {
                throw new EntityNotFoundException(String.format("Project id=%d not found.", startId));
            }
        }

        if ( startProject == null ) {
            List<Project> data = new ArrayList<>();
            return new DataPage<>(data, 0L, -1L);
        }

        Predicate predicate;
        switch ( type ) {
            case All:
                predicate = project.deletedAt.isNull()
                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
                                project.createdAt.eq(startProject.getCreatedAt()));
                break;
            case Assigned:
                predicate = project.deletedAt.isNull()
                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
                                project.createdAt.eq(startProject.getCreatedAt()))
                        .andAnyOf(project.administrators.contains(account), project.participants.contains(account));
                break;
            case Created:
                predicate = project.deletedAt.isNull()
                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
                                project.createdAt.eq(startProject.getCreatedAt()))
                        .and(project.author.eq(account));
                break;
            default:
                predicate = project.deletedAt.isNull();
                break;
        }

        List<Project> data = queryFactory.selectFrom(project)
                .where(predicate)
                .orderBy(project.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();

        Long nextStartProjectId;
        if ( data.isEmpty() || data.size() < pageSize + 1 ) {
            nextStartProjectId = -1L;
        }
        else {
            nextStartProjectId = data.remove(data.size() - 1).getProjectId();
        }

        return new DataPage<>(data, (long) data.size(), nextStartProjectId);
    }
}
