package sk.stu.fei.mproj.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@ApiModel
@Getter
@Setter
public class AccountDto extends AccountBaseDto {
    @ApiModelProperty(value = "Account creation date")
    private Date createdAt;
}
