package org.mycode.service;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Developer;
import org.mycode.repository.DeveloperRepository;
import org.mycode.repository.javaio.JavaIODeveloperRepositoryImpl;
import org.mycode.repository.jdbc.JDBCDeveloperRepositoryImpl;

import java.util.List;

public class DeveloperService {
    private static final Logger log = Logger.getLogger(DeveloperService.class);
    private JavaIODeveloperRepositoryImpl javaIORepo;
    private JDBCDeveloperRepositoryImpl jdbcRepo;
    private DeveloperRepository currentRepo;
    public DeveloperService() throws RepoStorageException {
        javaIORepo = new JavaIODeveloperRepositoryImpl();
        jdbcRepo = new JDBCDeveloperRepositoryImpl();
        this.currentRepo = jdbcRepo;
    }
    public DeveloperService(DeveloperRepository currentRepo) throws RepoStorageException {
        javaIORepo = (currentRepo instanceof JavaIODeveloperRepositoryImpl) ? (JavaIODeveloperRepositoryImpl) currentRepo : new JavaIODeveloperRepositoryImpl();
        jdbcRepo = (currentRepo instanceof JDBCDeveloperRepositoryImpl) ? (JDBCDeveloperRepositoryImpl) currentRepo : new JDBCDeveloperRepositoryImpl();
        this.currentRepo = currentRepo;
    }
    public void create(Developer model) throws Exception{
        currentRepo.create(model);
        log.debug("Service->Create");
    }
    public Developer getById(Long readID) throws Exception{
        Developer developer = currentRepo.getById(readID);
        log.debug("Service->Read");
        return developer;
    }
    public void update(Developer updatedModel) throws Exception{
        currentRepo.update(updatedModel);
        log.debug("Service->Update");
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
        log.debug("Service->Delete");
    }
    public List<Developer> getAll() throws Exception{
        List<Developer> developers = currentRepo.getAll();
        log.debug("Service->Get all");
        return developers;
    }
    public void changeStorage(TypeOfStorage typeOfStorage) {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCDeveloperRepositoryImpl){
                    this.currentRepo = javaIORepo;
                    log.debug("Service->Switch to files");
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIODeveloperRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                    log.debug("Service->Switch to DB");
                }
                break;
        }
    }
    public DeveloperRepository getCurrentRepo() {
        return currentRepo;
    }
}
