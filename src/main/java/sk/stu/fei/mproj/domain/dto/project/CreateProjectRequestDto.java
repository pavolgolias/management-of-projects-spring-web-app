package sk.stu.fei.mproj.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@Getter
@Setter
public class CreateProjectRequestDto {
    @ApiModelProperty(value = "Project name", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "Project description", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Administrator accounts ids")
    private List<Long> administratorAccountIds = new ArrayList<>();

    @ApiModelProperty(value = "Participant accounts ids")
    private List<Long> participantsAccountIds = new ArrayList<>();
}