package sk.stu.fei.mproj.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel("Login request object")
@Getter
@Setter
public class LoginRequest {
    @ApiModelProperty(value = "Email address", required = true)
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(value = "Password", required = true)
    @NotBlank
    private String password;
}
