package sk.stu.fei.mproj.domain.dto.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import sk.stu.fei.mproj.domain.dto.account.AccountBaseDto;

import java.util.Date;

@ApiModel
@Getter
@Setter
public class CommentDto {
    @ApiModelProperty(value = "Comment ID", required = true)
    private Long commentId;

    @ApiModelProperty(value = "Content of the comment", required = true)
    private String text;

    @ApiModelProperty(value = "Last update timestamp", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "Comment author", required = true)
    private AccountBaseDto author;
}
