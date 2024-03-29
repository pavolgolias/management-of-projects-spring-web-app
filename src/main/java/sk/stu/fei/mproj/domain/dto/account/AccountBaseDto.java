package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class AccountBaseDto {
    @ApiModelProperty(value = "Account ID", required = true)
    private Long accountId;

    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Email address", required = true)
    private String email;

    @ApiModelProperty(value = "Account's avatar filename")
    private String avatarFilename;

    @ApiModelProperty(value = "Account's static avatar path", required = true)
    private String staticAvatarFilename;
}
