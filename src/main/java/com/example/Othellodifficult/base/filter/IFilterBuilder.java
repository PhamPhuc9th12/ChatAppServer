package com.example.Othellodifficult.base.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface IFilterBuilder<T> {
    Filter.FilterBuilder<T> search();
    Filter.FilterBuilder<T> filter();
    Filter.FilterBuilder<T> isContain(String fieldName, String value);
    Filter.FilterBuilder<T> isIn(String fieldName, Collection<Object> value);
    Filter.FilterBuilder<T> isNotIn(String fieldName, Collection<Object> values);
    Filter.FilterBuilder<T> isEqual(String fieldName, Object value);
    Filter.FilterBuilder<T> orderBy(String fieldName, String orderType);
    Page<T> getPage(Pageable pageable);
}
