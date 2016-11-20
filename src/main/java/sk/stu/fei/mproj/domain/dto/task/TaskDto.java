package sk.stu.fei.mproj.domain.dto.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;

import java.util.Date;

@ApiModel
@Getter
@Setter
public class TaskDto extends TaskBaseDto {
    @ApiModelProperty(value = "Completion date", required = true)
    private Date completionDate;

    @ApiModelProperty(value = "Aimed completion date", required = true)
    private Date aimedCompletionDate;

    @ApiModelProperty(value = "Time spent on task in millis", required = true)
    private Long timeSpentOnTaskInMillis;

    @ApiModelProperty(value = "Aimed time for the task in millis", required = true)
    private Long timeEstimatedForTaskInMillis;

    @ApiModelProperty(value = "Task progress in %", required = true)
    private Integer progress;

    @ApiModelProperty(value = "Author of the task", required = true)
    private AccountDto author;

    @ApiModelProperty(value = "Last update timestamp", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "Task Id", required = true)
    private Long taskId;
}
