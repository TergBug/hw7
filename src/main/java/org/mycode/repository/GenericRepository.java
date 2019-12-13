package org.mycode.repository;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;

import java.util.List;

public interface GenericRepository<T, ID> {
    void create(T model) throws InvalidRepoFileException, NotUniquePrimaryKeyException, NoSuchEntryException;
    T getById(ID readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException;
    void update(T updatedModel) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException;
    void delete(ID deletedEntry) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException;
    List<T> getAll() throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException;
}