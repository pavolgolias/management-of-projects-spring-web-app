package sk.stu.fei.mproj.domain.dto.task;

        import io.swagger.annotations.ApiModel;
        import io.swagger.annotations.ApiModelProperty;
        import lombok.Getter;
        import lombok.Setter;
        import sk.stu.fei.mproj.domain.dto.account.AccountBaseDto;
        import sk.stu.fei.mproj.domain.dto.account.AccountDto;
        import sk.stu.fei.mproj.domain.dto.project.ProjectBaseDto;
        import sk.stu.fei.mproj.domain.enums.TaskPriority;
        import sk.stu.fei.mproj.domain.enums.TaskStatus;
        import sk.stu.fei.mproj.domain.enums.TaskType;

        import javax.persistence.*;
        import java.util.Date;

/**
 * Created by Patrik on 7.11.2016.
 */
@ApiModel
@Getter
@Setter
public class TaskDto {

    @ApiModelProperty(value = "Name", required = true)
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    private String description;

    @ApiModelProperty(value = "Task status", required = true)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ApiModelProperty(value = "Task type", required = true)
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ApiModelProperty(value = "Task priority", required = true)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ApiModelProperty(value = "Completion date", required = true)
    private Date completionDate;

    @ApiModelProperty(value = "Aimed completion date", required = true)
    private Date aimedCompletionDate;

    @ApiModelProperty(value = "Date oaf creation", required = true)
    private Date createdAt;

    @ApiModelProperty(value = "Date of last update", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "Time spent on task in millis", required = true)
    private Long timeSpentOnTaskInMillis;

    @ApiModelProperty(value = "Aimed time for the task in millis", required = true)
    private Long timeEstimatedForTaskInMillis;

    @ApiModelProperty(value = "Assigned account to the task", required = true)
    private AccountDto assignee;

    @ApiModelProperty(value = "Creator of the task", required = true)
    private AccountDto creator;

    @ApiModelProperty(value = "Basic information about the project", required = true)
    private ProjectBaseDto project;

}
