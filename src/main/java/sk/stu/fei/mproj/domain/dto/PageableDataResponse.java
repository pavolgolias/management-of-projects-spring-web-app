package sk.stu.fei.mproj.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class PageableDataResponse<T> extends DataResponse<T> {
    @ApiModelProperty(value = "Size of the page", required = true)
    private Long pageSize;
    @ApiModelProperty(value = "Next id where to start the search", required = true)
    private Long nextId;

    public PageableDataResponse(T data, Long pageSize, Long nextId) {
        super(data);
        this.pageSize = pageSize;
        this.nextId = nextId;
    }
}
