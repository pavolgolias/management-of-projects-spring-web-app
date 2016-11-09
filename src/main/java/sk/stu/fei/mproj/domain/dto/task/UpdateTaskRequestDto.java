package sk.stu.fei.mproj.domain.dto.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskStatus;
import sk.stu.fei.mproj.domain.enums.TaskType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * Created by Patrik on 09/11/2016.
 */
@ApiModel
@Getter
@Setter
public class UpdateTaskRequestDto extends CreateTaskRequestDto{
}
