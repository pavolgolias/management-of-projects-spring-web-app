package sk.stu.fei.mproj.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class DataResponse<T> {
    @ApiModelProperty(value = "Response data")
    private T data;

    public DataResponse() {
    }

    public DataResponse(T data) {
        this.data = data;
    }
}
