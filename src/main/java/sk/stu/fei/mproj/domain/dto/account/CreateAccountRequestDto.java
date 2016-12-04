package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
public class CreateAccountRequestDto {
    @ApiModelProperty(value = "First name", required = true)
    @NotBlank
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    @NotBlank
    private String lastName;

    @ApiModelProperty(value = "Email address", required = true)
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(value = "Password", required = true)
    @NotBlank
    private String password;

    @ApiModelProperty(value = "Repeat password", required = true)
    @NotBlank
    private String repeatPassword;

    @ApiModelProperty(value = "Account's static avatar path")
    private String staticAvatarFilename;
}
