package sk.stu.fei.mproj.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class ProjectDto {
    @ApiModelProperty(value = "Project ID", required = true)
    private Long projectId;

    @ApiModelProperty(value = "Name", required = true)
    private String name;

    @ApiModelProperty(value = "Description", required = true)
    private String description;

}
