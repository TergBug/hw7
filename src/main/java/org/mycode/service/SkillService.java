package org.mycode.service;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.repository.javaio.JavaIOSkillRepositoryImpl;
import org.mycode.repository.jdbc.JDBCSkillRepositoryImpl;

import java.util.List;

public class SkillService {
    private JavaIOSkillRepositoryImpl javaIORepo = new JavaIOSkillRepositoryImpl();
    private JDBCSkillRepositoryImpl jdbcRepo = new JDBCSkillRepositoryImpl();
    private SkillRepository currentRepo;
    public SkillService() throws RepoStorageException {
        this.currentRepo = jdbcRepo;
    }
    public void create(Skill model) throws Exception{
        currentRepo.create(model);
    }
    public Skill getById(Long readID) throws Exception{
        return currentRepo.getById(readID);
    }
    public void update(Skill updatedModel) throws Exception{
        currentRepo.update(updatedModel);
    }
    public void delete(Long deletedEntry) throws Exception{
        currentRepo.delete(deletedEntry);
    }
    public List<Skill> getAll() throws Exception{
        return currentRepo.getAll();
    }
    public void changeStorage(TypeOfStorage typeOfStorage) {
        switch (typeOfStorage){
            case FILES:
                if(this.currentRepo instanceof JDBCSkillRepositoryImpl){
                    this.currentRepo = javaIORepo;
                }
                break;
            case DATABASE:
                if(this.currentRepo instanceof JavaIOSkillRepositoryImpl){
                    this.currentRepo = jdbcRepo;
                }
                break;
        }
    }
}
