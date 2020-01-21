package org.mycode.repository.javaio;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.repository.DeveloperRepository;
import org.mycode.util.JavaIOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JavaIODeveloperRepositoryImpl implements DeveloperRepository {
    private static final Logger log = Logger.getLogger(JavaIODeveloperRepositoryImpl.class);
    private final String PATTERN_OF_ENTRY = "<{*-1-*}{-2-}{-3-}{-4-}{-5-}>";
    private final String VALIDATION_PATTERN = "<\\{\\*\\d+\\*}\\{.*?}\\{.*?}\\{(\\[\\d+\\])*}\\{\\[\\d+\\]}>";
    private JavaIOSkillRepositoryImpl skillRepo = new JavaIOSkillRepositoryImpl();
    private JavaIOAccountRepositoryImpl accountRepo = new JavaIOAccountRepositoryImpl();
    private File repo;
    public JavaIODeveloperRepositoryImpl(){
        repo = JavaIOUtils.getDeveloperRepo();
    }
    private Developer strMasToDeveloper(String[] mas) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {
        Set<Skill> skills = new HashSet<>();
        String[] skillsFK = mas[3].split("\\s");
        for (String oneSkillFK : skillsFK) {
            skills.add(skillRepo.getById(Long.parseLong(oneSkillFK)));
        }
        return new Developer(Long.parseLong(mas[0]), mas[1], mas[2], skills, accountRepo.getById(Long.parseLong(mas[4])));
    }
    private String[] developerToStrMas(Developer developer) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {
        StringBuilder skillsStr = new StringBuilder();
        for (Skill skill : developer.getSkills()) {
            skillsStr.append(skillRepo.getById(skill.getId()).getId() + " ");
        }
        skillsStr.deleteCharAt(skillsStr.length()-1);
        String accountStr = accountRepo.getById(developer.getAccount().getId()).getId().toString();
        return new String[]{developer.getId().toString(), developer.getFirstName(), developer.getLastName(), skillsStr.toString(), accountStr};
    }
    @Override
    public void create(Developer model) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {
        if(!repo.exists()) {
            try {
                repo.createNewFile();
            } catch (IOException e) {
                log.error("Cannot create new file", e);
                e.printStackTrace();
            }
        }
        if(model.getId()==null || model.getId()<1) {
            model.setId(JavaIOUtils.generateAutoIncrId(repo, VALIDATION_PATTERN));
        }
        else if(JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN).stream().anyMatch(el -> el[0].equals(model.getId().toString()))){
            log.warn("Not unique primary key: "+model.getId());
            throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        }
        StringBuilder skillForeignKeys = new StringBuilder();
        for (Skill skill : model.getSkills()) {
            skillForeignKeys.append("["+skillRepo.getById(skill.getId()).getId()+"]");
        }
        String accountForeignKey = "["+accountRepo.getById(model.getAccount().getId()).getId()+"]";
        String entry = PATTERN_OF_ENTRY.replace("-1-", String.valueOf(model.getId()))
                .replace("-2-", model.getFirstName())
                .replace("-3-", model.getLastName())
                .replace("-4-", skillForeignKeys.toString())
                .replace("-5-", accountForeignKey);
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
            log.debug("Create entry(file): "+model);
        } catch (IOException e) {
            log.error("Cannot write to file", e);
            e.printStackTrace();
        }
    }
    @Override
    public Developer getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN).stream()
                .filter(el -> el[0].equals(readID.toString()))
                .collect(Collectors.toList());
        if(content.size()==0){
            log.warn("No such entry with ID: "+readID);
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        else if(content.size()>1){
            log.warn("Not unique primary key: "+readID);
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        log.debug("Read entry(file) with ID: "+readID);
        return strMasToDeveloper(content.get(0));
    }
    @Override
    public void update(Developer updatedModel) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        boolean isExist = false;
        for (int i = 0; i < content.size(); i++) {
            if(content.get(i)[0].equals(updatedModel.getId().toString())){
                isExist = true;
                content.set(i, developerToStrMas(updatedModel));
            }
        }
        if(!isExist){
            log.warn("No such entry: "+updatedModel);
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(content);
        log.debug("Update entry(file): "+updatedModel);
    }
    @Override
    public void delete(Long deletedID) throws RepoStorageException, NoSuchEntryException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        if(!content.removeIf(el -> el[0].equals(deletedID.toString()))){
            log.warn("No such entry with ID: "+deletedID);
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(content);
        log.debug("Delete entry(file) with ID: "+deletedID);
    }
    @Override
    public List<Developer> getAll() throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = JavaIOUtils.getContentFromFile(repo, VALIDATION_PATTERN);
        List<Developer> developers = new ArrayList<>();
        for (String[] strings : content) {
            developers.add(strMasToDeveloper(strings));
        }
        log.debug("Read all entries(file)");
        return developers;
    }
    private void setAll(List<String[]> listOfDevelopersInStrMas){
        StringBuilder content = new StringBuilder();
        for (String[] developerStrMas : listOfDevelopersInStrMas){
            String skillForeignKeys = "["+developerStrMas[3].replaceAll("\\s", "][")+"]";
            String accountForeignKey = "["+developerStrMas[4]+"]";
            content.append(PATTERN_OF_ENTRY.replace("-1-", String.valueOf(developerStrMas[0]))
                    .replace("-2-", developerStrMas[1])
                    .replace("-3-", developerStrMas[2])
                    .replace("-4-", skillForeignKeys)
                    .replace("-5-", accountForeignKey));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) {
            log.error("Cannot write to file", e);
            e.printStackTrace();
        }
    }
}
