package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
public class UpdatePasswordRequestDto {
    @ApiModelProperty(value = "Old password", required = true)
    @NotNull
    private String oldPassword;

    @ApiModelProperty(value = "New password", required = true)
    @NotNull
    private String newPassword;

    @ApiModelProperty(value = "Repeat new password", required = true)
    @NotNull
    private String repeatNewPassword;
}
