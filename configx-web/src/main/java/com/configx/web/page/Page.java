/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Multimaps;

public class Page<K, T> {

    private final long total;

    private final PageRequest request;

    private final List<T> content = new ArrayList<T>();

    private Map<K, Collection<T>> contentMap = null;

    public Page(long total, PageRequest request, List<T> content) {
        this.total = total;
        this.request = request;
        this.content.addAll(content);
    }

    public Page(List<T> totalContent, PageRequest request) {
        this.request = request;

        if (totalContent == null) {
            this.total = 0;
        } else {
            this.total = totalContent.size();

            int startIndex = (request.getPage() - 1) * request.getSize();
            startIndex = Math.max(0, startIndex);
            startIndex = Math.min(totalContent.size(), startIndex);

            int endIndex = request.getPage() * request.getSize();
            endIndex = Math.max(0, endIndex);
            endIndex = Math.min(totalContent.size(), endIndex);
            for (int index = startIndex; index < endIndex; index++) {
                this.content.add(totalContent.get(index));
            }
        }
    }

    /**
     * 返回页码
     * 
     * @return
     */
    public int getPageNumber() {
        return request == null ? 0 : request.getPage();
    }

    /**
     * 返回页大小
     * 
     * @return
     */
    public int getPageSize() {
        return request == null ? 0 : request.getSize();
    }

    /**
     * 返回页数
     * 
     * @return
     */
    public int getPageCount() {
        return total == 0 || getPageSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getPageSize());
    }

    /**
     * 返回总记录数
     * 
     * @return
     */
    public long getTotalElements() {
        return total;
    }

    /**
     * 返回分页内容
     * 
     * @return
     */
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * 返回分页内容是否为空
     * 
     * @return
     */
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /**
     * 返回是否是首页
     * 
     * @return
     */
    public boolean isFirst() {
        return !hasPrevious();
    }

    /**
     * 返回是否是末页
     * 
     * @return
     */
    public boolean isLast() {
        return !hasNext();
    }

    /**
     * 判断是否有前一页
     * 
     * @return
     */
    public boolean hasPrevious() {
        return getPageNumber() > 0;
    }

    /**
     * 判断是否有下一页
     * 
     * @return
     */
    public boolean hasNext() {
        return getPageNumber() + 1 < getPageCount();
    }

    /**
     * 将结果集进行分组
     * 
     * @param function
     * @return
     */
    public Page<K, T> group(Function<T, K> function) {
        this.contentMap = Multimaps.index(content, function).asMap();
        return this;
    }

    /**
     * 返回分组的结果集
     * 
     * @return
     */
    public Map<K, Collection<T>> getContentMap() {
        return contentMap == null ? Collections.<K, Collection<T>> emptyMap() : contentMap;
    }
}
