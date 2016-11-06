package sk.stu.fei.mproj.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel
@Getter
@Setter
public class CreateProjectRequestDto {

    @ApiModelProperty(value = "Name", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    @NotBlank
    private String description;

}