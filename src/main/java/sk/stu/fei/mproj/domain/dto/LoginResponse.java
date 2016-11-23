package sk.stu.fei.mproj.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.enums.AccountRole;

@ApiModel("Successful login response")
@Getter
@Setter
public class LoginResponse {
    @ApiModelProperty(value = "Json web token (JWT)", required = true)
    private String token;

    @ApiModelProperty(value = "Authenticated user role")
    private AccountRole role;

    @ApiModelProperty(value = "Authenticated user", required = true)
    private AccountDto account;
}
