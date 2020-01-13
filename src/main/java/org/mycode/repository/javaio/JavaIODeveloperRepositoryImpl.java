package org.mycode.repository.javaio;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.repository.AccountRepository;
import org.mycode.repository.DeveloperRepository;
import org.mycode.repository.SkillRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaIODeveloperRepositoryImpl implements DeveloperRepository {
    private final String patternOfEntry = "<{*-1-*}{-2-}{-3-}{-4-}{-5-}>";
    private final String validationPattern = "<\\{\\*\\d+\\*}\\{.*?}\\{.*?}\\{(\\[\\d+\\])*}\\{\\[\\d+\\]}>";
    private final String linkToFile = "./src/main/resources/filestxt/developers.txt";
    private File repo;
    private SkillRepository skillRepo = new JavaIOSkillRepositoryImpl();
    private AccountRepository accountRepo = new JavaIOAccountRepositoryImpl();
    public JavaIODeveloperRepositoryImpl(){
        repo = new File(linkToFile);
    }
    private List<String[]> getContentFromFile(File file, String validPattern) throws InvalidRepoFileException {
        if(!file.exists()){
            throw new InvalidRepoFileException("Extracting of content from file is failed");
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
            contentTable.add(new String[5]);
            contentTable.get(contentTable.size()-1)[0] = findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", "");
            contentTable.get(contentTable.size()-1)[1] = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
            contentTable.get(contentTable.size()-1)[2] = findInMatcherByIndex(innerMatcher, 3).group().replaceAll("[{}]", "");
            contentTable.get(contentTable.size()-1)[3] = findInMatcherByIndex(innerMatcher, 4).group().replaceAll("]\\[", " ").replaceAll("[{\\[\\]}]", "");
            contentTable.get(contentTable.size()-1)[4] = findInMatcherByIndex(innerMatcher, 5).group().replaceAll("[{\\[\\]}]", "");
        }
        if(contentTable.size()==0 && content.length()>0){
            throw new InvalidRepoFileException("Extracting of content from file is failed");
        }
        return contentTable;
    }
    private Developer strMasToDeveloper(String[] mas) throws InvalidRepoFileException, NotUniquePrimaryKeyException, NoSuchEntryException {
        Set<Skill> skills = new HashSet<>();
        String[] skillsFK = mas[3].split("\\s");
        for (String oneSkillFK : skillsFK) {
            skills.add(skillRepo.getById(Long.parseLong(oneSkillFK)));
        }
        AccountRepository accountRepository = new JavaIOAccountRepositoryImpl();
        return new Developer(Long.parseLong(mas[0]), mas[1], mas[2], skills, accountRepository.getById(Long.parseLong(mas[4])));
    }
    private String[] developerToStrMas(Developer developer) throws InvalidRepoFileException, NotUniquePrimaryKeyException, NoSuchEntryException {
        StringBuilder skillsStr = new StringBuilder();
        for (Skill skill : developer.getSkills()) {
            skillsStr.append(skillRepo.getById(skill.getId()).getId() + " ");
        }
        skillsStr.deleteCharAt(skillsStr.length()-1);
        String accountStr = accountRepo.getById(developer.getAccount().getId()).getId().toString();
        return new String[]{developer.getId().toString(), developer.getFirstName(), developer.getLastName(), skillsStr.toString(), accountStr};
    }
    @Override
    public void create(Developer model) throws InvalidRepoFileException, NotUniquePrimaryKeyException, NoSuchEntryException {
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
        StringBuilder skillForeignKeys = new StringBuilder();
        for (Skill skill : model.getSkills()) {
            skillForeignKeys.append("["+skillRepo.getById(skill.getId()).getId()+"]");
        }
        String accountForeignKey = "["+accountRepo.getById(model.getAccount().getId()).getId()+"]";
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getId()))
                .replace("-2-", model.getFirstName())
                .replace("-3-", model.getLastName())
                .replace("-4-", skillForeignKeys.toString())
                .replace("-5-", accountForeignKey);
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private Long generateAutoIncrId() throws InvalidRepoFileException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        long id = 1L;
        if (content.size()!=0){
            content.sort(Comparator.comparing(el -> el[0]));
            id = Long.parseLong(content.get(content.size()-1)[0])+1;
        }
        return id;
    }
    @Override
    public Developer getById(Long readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = getContentFromFile(repo, validationPattern).stream()
                .filter(el -> el[0].equals(readID.toString()))
                .collect(Collectors.toList());
        if(content.size()==0){
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        else if(content.size()>1){
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        return strMasToDeveloper(content.get(0));
    }
    @Override
    public void update(Developer updatedModel) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        boolean isExist = false;
        for (int i = 0; i < content.size(); i++) {
            if(content.get(i)[0].equals(updatedModel.getId().toString())){
                isExist = true;
                content.set(i, developerToStrMas(updatedModel));
            }
        }
        if(!isExist){
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(content);
    }
    @Override
    public void delete(Long deletedID) throws InvalidRepoFileException, NoSuchEntryException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        if(!content.removeIf(el -> el[0].equals(deletedID.toString()))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(content);
    }
    @Override
    public List<Developer> getAll() throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<String[]> content = getContentFromFile(repo, validationPattern);
        List<Developer> developers = new ArrayList<>();
        for (String[] strings : content) {
            developers.add(strMasToDeveloper(strings));
        }
        return developers;
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<String[]> listOfDevelopersInStrMas){
        StringBuilder content = new StringBuilder();
        for (String[] developerStrMas : listOfDevelopersInStrMas){
            String skillForeignKeys = "["+developerStrMas[3].replaceAll("\\s", "][")+"]";
            String accountForeignKey = "["+developerStrMas[4]+"]";
            content.append(patternOfEntry.replace("-1-", String.valueOf(developerStrMas[0]))
                    .replace("-2-", developerStrMas[1])
                    .replace("-3-", developerStrMas[2])
                    .replace("-4-", skillForeignKeys)
                    .replace("-5-", accountForeignKey));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
