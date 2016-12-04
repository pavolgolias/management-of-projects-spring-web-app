package sk.stu.fei.mproj.domain.dto.comment;

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
public class CommentDto {

    @ApiModelProperty(value = "Comment ID", required = true)
    private Long commentId;

    @ApiModelProperty(value = "Text", required = true)
    private String text;

    @ApiModelProperty(value = "Last update timestamp")
    private Date updatedAt;
}
