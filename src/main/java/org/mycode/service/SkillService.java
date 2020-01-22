package org.mycode.service;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.repository.javaio.JavaIOSkillRepositoryImpl;
import org.mycode.repository.jdbc.JDBCSkillRepositoryImpl;

import java.util.List;

public class SkillService {
    private static final Logger log = Logger.getLogger(SkillService.class);
    private JavaIOSkillRepositoryImpl javaIORepo = new JavaIOSkillRepositoryImpl();
    private JDBCSkillRepositoryImpl jdbcRepo = new JDBCSkillRepositoryImpl();
    private SkillRepository currentRepo;
    public SkillService() throws RepoStorageException {
        this.currentRepo = jdbcRepo;
    }
    public void create(Skill model) throws Exception{
        currentRepo.create(model);
        log.debug("Service->Create");
    }
    public Skill getById(Long readID) throws Exception{
        Skill skill = currentRepo.getById(readID);
        log.debug("Service->Read");
        return skill;
    }
    public void update(Skill updatedModel) throws Exception{
        currentRepo.update(updatedModel);
        log.debug("Service->Update");
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
        log.debug("Service->Delete");
    }
    public List<Skill> getAll() throws Exception{
        List<Skill> skills = currentRepo.getAll();
        log.debug("Service->Get all");
        return skills;
    }
    public void changeStorage(TypeOfStorage typeOfStorage) {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCSkillRepositoryImpl){
                    this.currentRepo = javaIORepo;
                    log.debug("Service->Switch to files");
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIOSkillRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                    log.debug("Service->Switch to DB");
                }
                break;
        }
    }
}
