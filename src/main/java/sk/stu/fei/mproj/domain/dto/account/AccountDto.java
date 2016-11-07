package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class AccountDto extends AccountBaseDto {
    @ApiModelProperty(value = "Email address", required = true)
    private String email;
}
