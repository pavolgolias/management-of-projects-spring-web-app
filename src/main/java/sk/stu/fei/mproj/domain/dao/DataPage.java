package sk.stu.fei.mproj.domain.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class DataPage<T extends Collection> {
    private T page;
    private Long pageSize;
    private Long nextId;

    public DataPage(T page, Long pageSize, Long nextId) {
        this.page = page;
        this.pageSize = pageSize;
        this.nextId = nextId;
    }
}
