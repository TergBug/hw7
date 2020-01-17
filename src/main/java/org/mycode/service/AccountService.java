package org.mycode.service;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.repository.AccountRepository;
import org.mycode.repository.javaio.JavaIOAccountRepositoryImpl;
import org.mycode.repository.jdbc.JDBCAccountRepositoryImpl;

import java.util.List;

public class AccountService {
    private JavaIOAccountRepositoryImpl javaIORepo = new JavaIOAccountRepositoryImpl();
    private JDBCAccountRepositoryImpl jdbcRepo = new JDBCAccountRepositoryImpl();
    private AccountRepository currentRepo;
    public AccountService() throws RepoStorageException {
        this.currentRepo = jdbcRepo;
    }
    public void create(Account model) throws Exception{
        currentRepo.create(model);
    }
    public Account getById(Long readID) throws Exception{
        return currentRepo.getById(readID);
    }
    public void update(Account updatedModel) throws Exception{
        currentRepo.update(updatedModel);
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
    }
    public List<Account> getAll() throws Exception{
        return currentRepo.getAll();
    }
    public void changeStorage(TypeOfStorage typeOfStorage) throws RepoStorageException {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCAccountRepositoryImpl){
                    this.currentRepo = javaIORepo;
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIOAccountRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                }
                break;
        }
    }
}
