package org.mycode.service;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Developer;
import org.mycode.repository.DeveloperRepository;
import org.mycode.repository.javaio.JavaIODeveloperRepositoryImpl;
import org.mycode.repository.jdbc.JDBCDeveloperRepositoryImpl;

import java.util.List;

public class DeveloperService {
    private JavaIODeveloperRepositoryImpl javaIORepo = new JavaIODeveloperRepositoryImpl();
    private JDBCDeveloperRepositoryImpl jdbcRepo = new JDBCDeveloperRepositoryImpl();
    private DeveloperRepository currentRepo;
    public DeveloperService() throws RepoStorageException {
        this.currentRepo = jdbcRepo;
    }
    public void create(Developer model) throws Exception{
        currentRepo.create(model);
    }
    public Developer getById(Long readID) throws Exception{
        return currentRepo.getById(readID);
    }
    public void update(Developer updatedModel) throws Exception{
        currentRepo.update(updatedModel);
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
    }
    public List<Developer> getAll() throws Exception{
        return currentRepo.getAll();
    }
    public void changeStorage(TypeOfStorage typeOfStorage) throws RepoStorageException {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCDeveloperRepositoryImpl){
                    this.currentRepo = javaIORepo;
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIODeveloperRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                }
                break;
        }
    }
}
