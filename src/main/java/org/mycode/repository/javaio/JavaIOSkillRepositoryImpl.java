package org.mycode.repository.javaio;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.util.JavaIOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JavaIOSkillRepositoryImpl implements SkillRepository {
    private final String PATTERN_OF_ENTRY = "<{*-1-*}{-2-}>";
    private final String VALIDATION_PATTERN = "<\\{\\*\\d+\\*}\\{.*?}>";
    private File repo;
    public JavaIOSkillRepositoryImpl(){
        repo = JavaIOUtils.getSkillRepo();
    }
    private Skill strMasToSkill(String[] mas){
        return new Skill(Long.parseLong(mas[0]), mas[1]);
    }
    private String[] skillToStrMas(Skill skill){
        return new String[]{skill.getId().toString(), skill.getName()};
    }
    @Override
    public void create(Skill model) throws RepoStorageException, NotUniquePrimaryKeyException {
        if(!repo.exists()) {
            try {
                repo.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }
        if(model.getId()==null || model.getId()<1) {
            model.setId(JavaIOUtils.generateAutoIncrId(repo, VALIDATION_PATTERN));
        }
        else if(JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN).stream().anyMatch(el -> el[0].equals(model.getId().toString()))){
            throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        }
        String entry = PATTERN_OF_ENTRY.replace("-1-", String.valueOf(model.getId())).replace("-2-", model.getName());
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public Skill getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN).stream().
                filter(el -> el[0].equals(readID.toString())).
                collect(Collectors.toList());
        if(content.size()==0){
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        else if(content.size()>1){
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        return strMasToSkill(content.get(0));
    }
    @Override
    public void update(Skill updatedModel) throws RepoStorageException, NoSuchEntryException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        boolean isExist = false;
        for (int i = 0; i < content.size(); i++) {
            if(content.get(i)[0].equals(updatedModel.getId().toString())){
                isExist = true;
                content.set(i, skillToStrMas(updatedModel));
            }
        }
        if(!isExist){
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(content);
    }
    @Override
    public void delete(Long deletedID) throws NoSuchEntryException, RepoStorageException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        if(!content.removeIf(el -> el[0].equals(deletedID.toString()))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(content);
    }
    @Override
    public List<Skill> getAll() throws RepoStorageException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        return content.stream().map(this::strMasToSkill).collect(Collectors.toList());
    }
    private void setAll(List<String[]> listOfSkillsInStrMas){
        StringBuilder content = new StringBuilder();
        for (String[] skillStrMas : listOfSkillsInStrMas) {
            content.append(PATTERN_OF_ENTRY.replace("-1-", skillStrMas[0]).
                    replace("-2-", skillStrMas[1]));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
