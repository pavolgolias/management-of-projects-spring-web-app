package sk.stu.fei.mproj.domain;

import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.stu.fei.mproj.domain.dto.account.AccountBaseDto;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.project.CreateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.project.ProjectDto;
import sk.stu.fei.mproj.domain.dto.project.UpdateProjectRequestDto;
import sk.stu.fei.mproj.domain.dto.task.CreateTaskRequestDto;
import sk.stu.fei.mproj.domain.dto.task.TaskBaseDto;
import sk.stu.fei.mproj.domain.dto.task.TaskDto;
import sk.stu.fei.mproj.domain.dto.task.UpdateTaskRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;
import sk.stu.fei.mproj.domain.entities.Task;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Component
public class Mapper {
    private final MapperFactory mapperFactory;

    @Autowired
    public Mapper(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.configure(mapperFactory);
    }

    private void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(Account.class, AccountBaseDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreateAccountRequestDto.class, Account.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdateAccountRequestDto.class, Account.class)
                .byDefault()
                .register();
        mapperFactory.classMap(Project.class, ProjectDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreateProjectRequestDto.class, Project.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdateProjectRequestDto.class, Project.class)
                .byDefault()
                .register();
        mapperFactory.classMap(Task.class, TaskBaseDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(Task.class, TaskDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreateTaskRequestDto.class, Task.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdateTaskRequestDto.class, Task.class)
                .byDefault()
                .register();
    }

    public AccountDto toAccountDto(@NotNull Account account) {
        Objects.requireNonNull(account);

        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

    public Account toAccount(@NotNull CreateAccountRequestDto dto) {
        Objects.requireNonNull(dto);

        return mapperFactory.getMapperFacade().map(dto, Account.class);
    }

    public void fillAccount(@NotNull UpdateAccountRequestDto dto, @NotNull Account account) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(account);

        mapperFactory.getMapperFacade().map(dto, account);
    }

    public Project toProject(@NotNull CreateProjectRequestDto dto) {
        Objects.requireNonNull(dto);

        return mapperFactory.getMapperFacade().map(dto, Project.class);
    }

    public ProjectDto toProjectDto(@NotNull Project project) {
        Objects.requireNonNull(project);

        return mapperFactory.getMapperFacade().map(project, ProjectDto.class);
    }

    public void fillProject(@NotNull UpdateProjectRequestDto dto, @NotNull Project project) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(project);

        mapperFactory.getMapperFacade().map(dto, project);
    }

    public List<AccountDto> toAccountDtoList(@NotNull List<Account> accounts) {
        Objects.requireNonNull(accounts);

        return mapperFactory.getMapperFacade().mapAsList(accounts, AccountDto.class);
    }

    public TaskDto toTaskDto(@NotNull Task task) {
        Objects.requireNonNull(task);

        return mapperFactory.getMapperFacade().map(task, TaskDto.class);
    }

    public Task toTask(@NotNull CreateTaskRequestDto dto) {
        Objects.requireNonNull(dto);

        return mapperFactory.getMapperFacade().map(dto, Task.class);
    }

    public void fillTask(@NotNull UpdateTaskRequestDto dto, @NotNull Task task) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(task);

        mapperFactory.getMapperFacade().map(dto, task);
    }

    public List<ProjectDto> toProjectDtoList(@NotNull List<Project> projects) {
        Objects.requireNonNull(projects);

        return mapperFactory.getMapperFacade().mapAsList(projects, ProjectDto.class);
    }
}
