package org.mycode.service;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.repository.AccountRepository;
import org.mycode.repository.javaio.JavaIOAccountRepositoryImpl;
import org.mycode.repository.jdbc.JDBCAccountRepositoryImpl;

import java.util.List;

public class AccountService {
    private static final Logger log = Logger.getLogger(AccountService.class);
    private JavaIOAccountRepositoryImpl javaIORepo;
    private JDBCAccountRepositoryImpl jdbcRepo;
    private AccountRepository currentRepo;
    public AccountService() throws RepoStorageException {
        javaIORepo = new JavaIOAccountRepositoryImpl();
        jdbcRepo = new JDBCAccountRepositoryImpl();
        this.currentRepo = jdbcRepo;
    }
    public AccountService(AccountRepository currentRepo) throws RepoStorageException {
        javaIORepo = (currentRepo instanceof JavaIOAccountRepositoryImpl) ? (JavaIOAccountRepositoryImpl) currentRepo : new JavaIOAccountRepositoryImpl();
        jdbcRepo = (currentRepo instanceof JDBCAccountRepositoryImpl) ? (JDBCAccountRepositoryImpl) currentRepo : new JDBCAccountRepositoryImpl();
        this.currentRepo = currentRepo;
    }
    public void create(Account model) throws Exception{
        currentRepo.create(model);
        log.debug("Service->Create");
    }
    public Account getById(Long readID) throws Exception{
        Account account = currentRepo.getById(readID);
        log.debug("Service->Read");
        return account;
    }
    public void update(Account updatedModel) throws Exception{
        currentRepo.update(updatedModel);
        log.debug("Service->Update");
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
        log.debug("Service->Delete");
    }
    public List<Account> getAll() throws Exception{
        List<Account> accounts = currentRepo.getAll();
        log.debug("Service->Get all");
        return accounts;
    }
    public void changeStorage(TypeOfStorage typeOfStorage) {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCAccountRepositoryImpl){
                    this.currentRepo = javaIORepo;
                    log.debug("Service->Switch to files");
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIOAccountRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                    log.debug("Service->Switch to DB");
                }
                break;
        }
    }
    public AccountRepository getCurrentRepo() {
        return currentRepo;
    }
}
