package org.mycode.repository;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;

import java.util.List;

public interface GenericRepository<T, ID> {
    void create(T model) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException;
    T getById(ID readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException;
    void update(T updatedModel) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException;
    void delete(ID deletedEntry) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException;
    List<T> getAll() throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException;
}