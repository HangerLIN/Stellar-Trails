package util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import page.PageRequest;
import page.PageResponse;
import toolkit.BeanUtil;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Classname PageUtil
 * @Description 分页工具类
 * @Date 2023/10/30 11:40
 * @Created by lth
 */
public class PageUtil {
    /**
     * 默认页码
     */
    public static final Integer DEFAULT_PAGE_INDEX = 1;

    /**
     * 默认每页条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 最小每页条数
     */
    public static final Integer MIN_PAGE_SIZE = 1;

    /**
     * 获取页码
     * @param pageIndex
     * @return
     */
    public static Integer getPageIndex(Integer pageIndex){
        if (pageIndex == null || pageIndex < DEFAULT_PAGE_INDEX){
            return DEFAULT_PAGE_INDEX;
        }
        return pageIndex;
    }

    /**
     * 获取每页条数
     * @param pageSize
     * @return
     */
    public static Integer getPageSize(Integer pageSize){
        if (pageSize == null || pageSize < MIN_PAGE_SIZE){
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE){
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    /**
     * 获取分页偏移量
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static Integer getOffset(Integer pageIndex,Integer pageSize) {
        return (getPageIndex(pageIndex) - 1) * getPageSize(pageSize);
    }

    public static Page convert(PageRequest pageRequest) {
        return convert(pageRequest.getCurrent(), pageRequest.getSize());
    }

    public static Page convert(long current, long size) {
        return new Page(current, size);
    }

    public static PageResponse convert(IPage iPage) {
        return buildConventionPage(iPage);
    }

    public static <TARGET, ORIGINAL> PageResponse<TARGET> convert(IPage<ORIGINAL> iPage, Class<TARGET> targetClass) {
        iPage.convert(each -> BeanUtil.convert(each, targetClass));
        return buildConventionPage(iPage);
    }

    public static <TARGET, ORIGINAL> PageResponse<TARGET> convert(IPage<ORIGINAL> iPage, Function<? super ORIGINAL, ? extends TARGET> mapper) {
        List<TARGET> targetDataList = iPage.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PageResponse.<TARGET>builder()
                .current(iPage.getCurrent())
                .size(iPage.getSize())
                .records(targetDataList)
                .total(iPage.getTotal())
                .build();
    }

    private static PageResponse buildConventionPage(IPage iPage) {
        return PageResponse.builder()
                .current(iPage.getCurrent())
                .size(iPage.getSize())
                .records(iPage.getRecords())
                .total(iPage.getTotal())
                .build();
    }

}
