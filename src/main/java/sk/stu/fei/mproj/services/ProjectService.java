package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dao.ProjectDao;
import sk.stu.fei.mproj.domain.dto.project.CreateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.project.UpdateProjectRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;

@Service
@Transactional
public class ProjectService {

    private final ProjectDao projectDao;
    private final Mapper mapper;
    private final AuthorizationManager authorizationManager;

    @Autowired
    public ProjectService(Mapper mapper, ProjectDao projectDao, AuthorizationManager authorizationManager) {
        this.mapper = mapper;
        this.projectDao = projectDao;
        this.authorizationManager = authorizationManager;
    }

    @RoleSecured
    public Project createProject(@NotNull CreateProjectRequestDto dto) {
        Objects.requireNonNull(dto);

        if ( projectDao.findByName(dto.getName()) != null ) {
            throw new IllegalArgumentException(String.format("Name=%s is already used by another project.", dto.getName()));
        }

        Project project = mapper.toProject(dto);
        Account account = authorizationManager.getCurrentAccount();
        project.setAdministrators(new HashSet<Account>());
        project.getAdministrators().add(account);
        project.setAuthor(account);
        projectDao.persist(project);

        return project;
    }

    @RoleSecured
    public Project getProject(Long projectId) {
        return getOrElseThrowEntityNotFoundEx(projectId, projectDao, String.format("Project id=%d not found", projectId));
    }

    @RoleSecured
    public Project updateProject(Long projectId, @NotNull UpdateProjectRequestDto dto) {
        Objects.requireNonNull(dto);

        Project project = getProject(projectId);

        Project found = projectDao.findByName(dto.getName());
        if ( found != null && !found.equals(project) ) {
            throw new IllegalArgumentException(String.format("Name=%s is already used by another project.", dto.getName()));
        }

        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update project id=%d information", projectId)
        );
        mapper.fillProject(dto, project);
        projectDao.persist(project);

        return project;
    }

    @RoleSecured
    public void deleteProject(Long projectId) {
        Project project = getProject(projectId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to delete project id=%d", projectId)
        );
        projectDao.delete(project);
    }

    @RoleSecured
    public Project addAdministrator(Long projectId, Account account) {
        Project project = getProject(projectId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible add administrator for this project id=%d", projectId)
        );
        project.getAdministrators().add(account);
        projectDao.persist(project);

        return project;
    }

    @RoleSecured
    public Project addParticipant(Long projectId, Account account) {
        Project project = getProject(projectId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible add participant for this project id=%d", projectId)
        );

        if(project.getParticipants() != null){
            project.getParticipants().add(account);
        }else{
            project.setParticipants(new HashSet<Account>());
            project.getParticipants().add(account);
        }

        projectDao.persist(project);

        return project;
    }

    @RoleSecured
    public Project removeAdministrator(Long projectId, Account account) {
        Project project = getProject(projectId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible remove administrator for this project id=%d", projectId)
        );
        project.getAdministrators().remove(account);
        projectDao.persist(project);

        return project;
    }

    @RoleSecured
    public Project removeParticipant(Long projectId, Account account) {
        Project project = getProject(projectId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                project,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible remove participant for this project id=%d", projectId)
        );
        project.getParticipants().remove(account);
        projectDao.persist(project);

        return project;
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    private void checkUpdateEligibilityOrElseThrowSecurityEx(Project updateTarget, Account who, String exceptionMessage) {
        if ( !updateTarget.getAdministrators().contains(who) ) {
            throw new SecurityException(exceptionMessage);
        }
    }

}
