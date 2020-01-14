package org.mycode.repository.jdbc;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.repository.AccountRepository;

import java.util.List;

public class JDBCAccountRepositoryImpl implements AccountRepository {
    @Override
    public void create(Account model) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {

    }
    @Override
    public Account getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        return null;
    }
    @Override
    public void update(Account updatedModel) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {

    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {

    }
    @Override
    public List<Account> getAll() throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        return null;
    }
}
