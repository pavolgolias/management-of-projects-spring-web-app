package sk.stu.fei.mproj.domain.dto.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskStatus;
import sk.stu.fei.mproj.domain.enums.TaskType;

@ApiModel
@Getter
@Setter
public class TaskBaseDto {
    @ApiModelProperty(value = "Name", required = true)
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    private String description;

    @ApiModelProperty(value = "Task status", required = true)
    private TaskStatus status;

    @ApiModelProperty(value = "Task type", required = true)
    private TaskType type;

    @ApiModelProperty(value = "Task priority", required = true)
    private TaskPriority priority;

    @ApiModelProperty(value = "Assigned account to the task", required = true)
    private AccountDto assignee;
}
