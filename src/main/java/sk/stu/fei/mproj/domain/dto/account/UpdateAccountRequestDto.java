package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
public class UpdateAccountRequestDto {
    @ApiModelProperty(value = "First name", required = true)
    @NotNull
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    @NotNull
    private String lastName;
}
