package page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Classname PageResponse
 * @Description
 * @Date 2023/10/27 18:08
 * @Created by lth
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long current;
    private Long size = 10L;
    private Long total;
    private List<T> records = Collections.emptyList();

    public PageResponse(long current, long size) {
        this(current, size, 0);
    }

    public PageResponse(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }

    public PageResponse setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    /**
     * @Description
     * @param mapper 将一种类型的对象转换为另一种类型的对象
     * @return page.PageResponse<R>
     * @data 2023/10/27 19:11
     * @author lth
     */
    public <R> PageResponse<R> convert(Function<? super T, ? extends R> mapper) {
        PageResponse<R> response = new PageResponse<>(this.current, this.size, this.total);
        response.setRecords(this.records.stream().map(mapper).collect(Collectors.toList()));
        return response;
    }
}
