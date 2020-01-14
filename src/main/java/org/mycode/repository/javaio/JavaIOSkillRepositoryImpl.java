package org.mycode.repository.javaio;

import org.mycode.exceptions.RepoStorageException;
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
    private final String validationPattern = "<\\{\\*\\d+\\*}\\{.*?}>";
    private final String linkToFile = "./src/main/resources/filestxt/skills.txt";
    private File repo;
    public JavaIOSkillRepositoryImpl(){
        repo = new File(linkToFile);
    }
    private List<String[]> getContentFromFile(File file, String validPattern) throws RepoStorageException {
        if(!file.exists()){
            throw  new RepoStorageException("Extracting of content from file is failed");
        }
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file)){
            int c;
            while ((c=fr.read()) != -1) content.append((char) c);
        } catch (IOException e) { e.printStackTrace(); }
        List<String[]> contentTable = new ArrayList<>();
        Matcher outerMatcher = Pattern.compile(validPattern).matcher(content);
        Matcher innerMatcher;
        while (outerMatcher.find()){
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            contentTable.add(new String[2]);
            contentTable.get(contentTable.size()-1)[0] = findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", "");
            contentTable.get(contentTable.size()-1)[1] = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
        }
        if(contentTable.size()==0 && content.length()>0){
            throw  new RepoStorageException("Extracting of content from file is failed");
        }
        return contentTable;
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
            model.setId(generateAutoIncrId());
        }
        else if(getContentFromFile(repo, validationPattern).stream().anyMatch(el -> el[0].equals(model.getId().toString()))){
            throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        }
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getId())).replace("-2-", model.getName());
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private Long generateAutoIncrId() throws RepoStorageException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        long id = 1L;
        if (content.size()!=0){
            content.sort(Comparator.comparing(el -> el[0]));
            id = Long.parseLong(content.get(content.size()-1)[0])+1;
        }
        return id;
    }
    @Override
    public Skill getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = getContentFromFile(repo, validationPattern).stream().
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
        List<String[]> content = getContentFromFile(repo, validationPattern);
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
        List<String[]> content = getContentFromFile(repo, validationPattern);
        if(!content.removeIf(el -> el[0].equals(deletedID.toString()))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(content);
    }
    @Override
    public List<Skill> getAll() throws RepoStorageException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        return content.stream().map(this::strMasToSkill).collect(Collectors.toList());
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<String[]> listOfSkillsInStrMas){
        StringBuilder content = new StringBuilder();
        for (String[] skillStrMas : listOfSkillsInStrMas) {
            content.append(patternOfEntry.replace("-1-", skillStrMas[0]).
                    replace("-2-", skillStrMas[1]));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
