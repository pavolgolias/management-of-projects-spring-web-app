package sk.stu.fei.mproj.domain.dto.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel
@Getter
@Setter
public class UpdateCommentRequestDto {
    @ApiModelProperty(value = "Content of the comment", required = true)
    @NotBlank
    private String text;
}
