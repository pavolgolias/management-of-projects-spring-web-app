package sk.stu.fei.mproj.domain.dto.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskType;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Getter
@Setter
public class CreateTaskRequestDto {
    @ApiModelProperty(value = "Name", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Task type", required = true)
    @NotNull
    private TaskType type;

    @ApiModelProperty(value = "Task priority")
    private TaskPriority priority;

    @ApiModelProperty(value = "Aimed completion date")
    private Date aimedCompletionDate;

    @ApiModelProperty(value = "Aimed time for the task in millis")
    private Long timeEstimatedForTaskInMillis;

    @ApiModelProperty(value = "Id of the assigned user")
    private Long assigneeId;
}
