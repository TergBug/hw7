package org.mycode.repository.javaio;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaIOSkillRepositoryImpl implements SkillRepository {
    private final String patternOfEntry = "<{*-1-*}{-2-}>";
    private final String validationPattern = "(<\\{\\*\\d+\\*}\\{.*?}>.*)*";
    private final String linkToFile = "./src/main/resources/skills.txt";
    private File repo;
    public JavaIOSkillRepositoryImpl(){
        repo = new File(linkToFile);
    }
    private String getContentFromFile(File file, String validPattern){
        if(!file.exists()) return null;
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file)){
            int c;
            while ((c=fr.read()) != -1) content.append((char) c);
        } catch (IOException e) { e.printStackTrace(); }
        String outContent = content.toString().replaceAll("(\\r\\n)|(\\r)|(\\n)", "");
        return outContent.matches(validPattern) ? outContent : null;
    }
    @Override
    public void create(Skill model) throws InvalidRepoFileException, NotUniquePrimaryKeyException {
        if(!repo.exists()) {
            try {
                repo.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }
        if(model.getId()==null || model.getId()<1) {
            model.setId(generateAutoIncrId());
        }
        else if(getAll().stream().anyMatch(el-> el.getId().equals(model.getId()))){
            throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        }
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getId())).replace("-2-", model.getName());
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private Long generateAutoIncrId() throws InvalidRepoFileException {
        Long id = 1L;
        List<Skill> skills = getAll();
        if(skills.size()!=0){
            skills.sort(Comparator.comparingLong(Skill::getId));
            int index = 0;
            while (id.equals(skills.get((index == skills.size() - 1) ? index : index++).getId())){
                id++;
            }
        }
        return id;
    }
    @Override
    public Skill getById(Long readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Skill> listOfReadSkills = getAll().stream().filter(el-> el.getId().equals(readID)).collect(Collectors.toList());
        if(listOfReadSkills.size()==0){
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        else if(listOfReadSkills.size()>1){
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        return listOfReadSkills.get(0);
    }
    @Override
    public void update(Skill updatedModel) throws InvalidRepoFileException, NoSuchEntryException {
        List<Skill> listOfSkills = getAll();
        boolean isExist = false;
        for (int i = 0; i < listOfSkills.size(); i++) {
            if(listOfSkills.get(i).getId().equals(updatedModel.getId())){
                isExist = true;
                listOfSkills.set(i, updatedModel);
            }
        }
        if(!isExist){
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(listOfSkills);
    }
    @Override
    public void delete(Long deletedID) throws NoSuchEntryException, InvalidRepoFileException {
        List<Skill> listOfSkills = getAll();
        if(!listOfSkills.removeIf(el -> el.getId().equals(deletedID))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(listOfSkills);
    }
    @Override
    public List<Skill> getAll() throws InvalidRepoFileException {
        String content = getContentFromFile(repo, validationPattern);
        if(content==null){
            throw new InvalidRepoFileException("Extracting of content from file is failed");
        }
        Matcher outerMatcher = Pattern.compile("<\\{\\*\\d+?\\*}\\{.*?}>").matcher(content);
        Matcher innerMatcher;
        List<Skill> skills = new ArrayList<>();
        while (outerMatcher.find()){
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            Long id = Long.parseLong(findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", ""));
            String name = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
            skills.add(new Skill(id, name));
        }
        return skills;
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<Skill> listOfSkills){
        StringBuilder content = new StringBuilder();
        for (Skill skill : listOfSkills) {
            content.append(patternOfEntry.replace("-1-", String.valueOf(skill.getId())).
                    replace("-2-", skill.getName()));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
