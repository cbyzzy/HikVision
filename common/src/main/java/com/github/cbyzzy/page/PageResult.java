package com.github.cbyzzy.page;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.cbyzzy.exception.WebException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 封装的分页对象
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    //当前页
    private int pageNum;

    //每页的数量
    private int pageSize;

    //当前页的数量
    private int size;

    //总记录数
    private long total;

    //总页数
    private int pages;

    //结果集
    private List<T> list;

    // 是否最后一面
    private boolean isLastPage = false;

    private PageResult() {
    }

    public PageResult(List<T> list) {
        if (!(list instanceof Page)) {
            throw new WebException("请在使用分页对象前调用PageHelper.startPage(pageNum, pageSize);");
        }
        PageInfo<T> pageinfo = new PageInfo<>(list);

        pageNum = pageinfo.getPageNum();
        pageSize = pageinfo.getPageSize();
        size = pageinfo.getSize();
        total = pageinfo.getTotal();
        pages = pageinfo.getPages();
        isLastPage = pageinfo.isIsLastPage();
        this.list = pageinfo.getList();
    }

    /**
     * 如果PagteInfo信息为null， 则采用这个返回分页数据
     *
     * @return PageResult
     */
    public static <T> PageResult<T> nullPage() {
        PageResult<T> result = new PageResult<>();
        result.setList(new ArrayList<>());
        result.setPageNum(1);
        result.setPageSize(10);
        result.setTotal(0);
        result.setLastPage(true);
        result.setSize(0);
        return result;
    }

    /**
     * 根据数据创建一个PageResult对象
     *
     * @param list     集合数据
     * @param total    总记录数
     * @param pageNum  页码
     * @param pageSize 页数量
     * @return PageResult
     */
    public static <T> PageResult<T> createPage(List<T> list, long total, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPages((int) ((total + pageSize - 1) / pageSize));
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    /**
     * 用于分页对象转换
     *
     * @param page
     * @param fun
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> PageResult<R> convert(PageResult<T> page, Function<T, R> fun) {
        List<T> list = page.getList();
        List<R> collect = list.stream().map(fun).collect(Collectors.toList());
        PageResult<R> result = new PageResult<>();
        result.setList(collect);
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setLastPage(page.isLastPage());
        return result;
    }

    public int getPageNum() {
        return pageNum;
    }

    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    private void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    private void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    private void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    private void setList(List<T> list) {
        this.list = list;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
}
