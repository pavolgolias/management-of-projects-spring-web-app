package sk.stu.fei.mproj.domain.dto.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskStatus;
import sk.stu.fei.mproj.domain.enums.TaskType;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Getter
@Setter
public class UpdateTaskRequestDto {
    @ApiModelProperty(value = "Name", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Task status", required = true)
    @NotNull
    private TaskStatus status;

    @ApiModelProperty(value = "Task type", required = true)
    @NotNull
    private TaskType type;

    @ApiModelProperty(value = "Task priority", required = true)
    @NotNull
    private TaskPriority priority;

    @ApiModelProperty(value = "Aimed completion date", notes = "Setting to NULL (or not providing) will unset value")
    private Date aimedCompletionDate;

    @ApiModelProperty(value = "Time spent on task", notes = "Setting to NULL (or not providing) will unset value")
    private Long timeSpentOnTaskInMillis;

    @ApiModelProperty(value = "Aimed time for the task in millis", notes = "Setting to NULL (or not providing) will unset value")
    private Long timeEstimatedForTaskInMillis;

    @ApiModelProperty(value = "Task progress in %", required = true)
    @NotNull
    private Integer progress;

    @ApiModelProperty(value = "Id of the assigned user", notes = "Setting to NULL (or not providing) will unset value")
    private Long assigneeId;
}
