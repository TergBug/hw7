package org.mycode.repository;

import java.util.List;

public interface GenericRepository<T, ID> {
    void create(T model) throws Exception;
    T getById(ID readID) throws Exception;
    void update(T updatedModel) throws Exception;
    void delete(ID deletedEntry) throws Exception;
    List<T> getAll() throws Exception;
}