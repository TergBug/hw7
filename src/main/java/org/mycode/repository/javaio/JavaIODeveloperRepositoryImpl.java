package org.mycode.repository.javaio;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
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
    private final String validationPattern = "(<\\{\\*\\d+\\*}\\{.*?}\\{.*?}\\{(\\[\\d+\\])*}\\{\\[\\d+\\]}>.*)*";
    private final String linkToFile = "./src/main/resources/developers.txt";
    private File repo;
    public JavaIODeveloperRepositoryImpl(){
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
    public void create(Developer model) throws InvalidRepoFileException, NotUniquePrimaryKeyException, NoSuchEntryException {
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
        SkillRepository skillRepo = new JavaIOSkillRepositoryImpl();
        AccountRepository accountRepo = new JavaIOAccountRepositoryImpl();
        StringBuilder skillForeignKeys = new StringBuilder();
        for (Skill skill : model.getSkills()) {
            skillForeignKeys.append("["+skillRepo.getById(skill.getId()).getId()+"]");
        }
        String accountForeignKey = "["+accountRepo.getById(model.getAccount().getId()).getId()+"]";
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getId())).
                replace("-2-", model.getFirstName()).
                replace("-3-", model.getLastName()).
                replace("-4-", skillForeignKeys.toString()).
                replace("-5-", accountForeignKey);
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private Long generateAutoIncrId() throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        Long id = 1L;
        List<Developer> developers = getAll();
        if(developers.size()!=0){
            developers.sort(Comparator.comparingLong(Developer::getId));
            int index = 0;
            while (id.equals(developers.get((index == developers.size() - 1) ? index : index++).getId())){
                id++;
            }
        }
        return id;
    }
    @Override
    public Developer getById(Long readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Developer> listOfReadDevelopers = getAll().stream().filter(el -> el.getId().equals(readID)).collect(Collectors.toList());
        if(listOfReadDevelopers.size()==0){
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        if(listOfReadDevelopers.size()>1){
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        return listOfReadDevelopers.get(0);
    }
    @Override
    public void update(Developer updatedModel) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Developer> listOfDevelopers = getAll();
        boolean isExist = false;
        for (int i = 0; i < listOfDevelopers.size(); i++) {
            if(listOfDevelopers.get(i).getId().equals(updatedModel.getId())){
                isExist = true;
                listOfDevelopers.set(i, updatedModel);
            }
        }
        if(!isExist){
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(listOfDevelopers);
    }
    @Override
    public void delete(Long deletedID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Developer> listOfDevelopers = getAll();
        if(!listOfDevelopers.removeIf(el-> el.getId().equals(deletedID))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(listOfDevelopers);
    }
    @Override
    public List<Developer> getAll() throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        String content = getContentFromFile(repo, validationPattern);
        if(content==null){
            throw new InvalidRepoFileException("Extracting of content from file is failed");
        }
        Matcher outerMatcher = Pattern.compile("<\\{\\*\\d+\\*}\\{.*?}\\{.*?}\\{(\\[\\d+])*}\\{\\[\\d+]}>").matcher(content);
        Matcher innerMatcher;
        List<Developer> developers = new ArrayList<>();
        SkillRepository skillRepo = new JavaIOSkillRepositoryImpl();
        AccountRepository accountRepo = new JavaIOAccountRepositoryImpl();
        while (outerMatcher.find()){
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            Long id = Long.parseLong(findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", ""));
            String firstName = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
            String lastName = findInMatcherByIndex(innerMatcher, 3).group().replaceAll("[{}]", "");
            Account account = accountRepo.getById(Long.parseLong(findInMatcherByIndex(innerMatcher, 5).group().replaceAll("[{\\[\\]}]", "")));
            Set<Skill> skills = new HashSet<>();
            innerMatcher = Pattern.compile("\\[\\d+]").matcher(findInMatcherByIndex(innerMatcher, 4).group());
            while (innerMatcher.find()){
                skills.add(skillRepo.getById(Long.parseLong(innerMatcher.group().replaceAll("[\\[\\]]", ""))));
            }
            developers.add(new Developer(id, firstName, lastName, skills, account));
        }
        return developers;
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<Developer> listOfDevelopers){
        StringBuilder content = new StringBuilder();
        for (Developer developer : listOfDevelopers){
            StringBuilder skillForeignKeys = new StringBuilder();
            developer.getSkills().forEach(el -> skillForeignKeys.append("["+el.getId()+"]"));
            String accountForeignKey = "["+developer.getAccount().getId()+"]";
            content.append(patternOfEntry.replace("-1-", String.valueOf(developer.getId())).
                    replace("-2-", developer.getFirstName()).
                    replace("-3-", developer.getLastName()).
                    replace("-4-", skillForeignKeys.toString()).
                    replace("-5-", accountForeignKey));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
