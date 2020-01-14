package org.mycode.repository.jdbc;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Developer;
import org.mycode.repository.DeveloperRepository;

import java.util.List;

public class JDBCDeveloperRepositoryImpl implements DeveloperRepository {
    @Override
    public void create(Developer model) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {

    }
    @Override
    public Developer getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        return null;
    }
    @Override
    public void update(Developer updatedModel) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {

    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {

    }
    @Override
    public List<Developer> getAll() throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        return null;
    }
}
