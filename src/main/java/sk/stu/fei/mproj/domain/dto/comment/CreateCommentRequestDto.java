package sk.stu.fei.mproj.domain.dto.comment;

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
public class CreateCommentRequestDto {

    @ApiModelProperty(value = "Text", required = true)
    @NotBlank
    private String text;

}
