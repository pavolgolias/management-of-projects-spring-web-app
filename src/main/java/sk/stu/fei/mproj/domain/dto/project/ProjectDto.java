package sk.stu.fei.mproj.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.dto.account.AccountBaseDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @ApiModelProperty(value = "Project administrators", required = true)
    private List<AccountBaseDto> administrators = new ArrayList<>();

    @ApiModelProperty(value = "Project participants")
    private List<AccountBaseDto> participants = new ArrayList<>();

    @ApiModelProperty(value = "Last update timestamp")
    private Date updatedAt;

    @ApiModelProperty(value = "Project author", required = true)
    private AccountBaseDto author;
}
