package sk.stu.fei.mproj.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Patrik on 09/11/2016.
 */
@ApiModel
@Getter
@Setter
public class ProjectBaseDto {

    @ApiModelProperty(value = "Project ID", required = true)
    private Long projectId;

    @ApiModelProperty(value = "Name", required = true)
    private String name;
}
