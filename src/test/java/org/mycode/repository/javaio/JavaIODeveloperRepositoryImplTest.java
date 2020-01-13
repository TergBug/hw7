package org.mycode.repository.javaio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.model.Developer;
import org.mycode.model.Skill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JavaIODeveloperRepositoryImplTest {
    private JavaIODeveloperRepositoryImpl testedDeveloperRepo = new JavaIODeveloperRepositoryImpl();
    private String noSuchEntryExceptionStr = "org.mycode.exceptions.NoSuchEntryException: # of entry is failed";
    private String notUniquePrimaryKeyExceptionStr = "org.mycode.exceptions.NotUniquePrimaryKeyException: # of entry is failed";
    private File repoSkill = new File("./src/main/resources/filestxt/skills.txt");
    private File repoAccount = new File("./src/main/resources/filestxt/accounts.txt");
    private File repoDeveloper = new File("./src/main/resources/filestxt/developers.txt");
    private String newInfoInFileSkills = "<{*1*}{Java}><{*2*}{C#}><{*3*}{JDBC}>";
    private String newInfoInFileAccounts = "<{*1*}{LiXiao}{ACTIVE}><{*2*}{Din}{DELETED}><{*3*}{Geek}{BANNED}>";
    private String newInfoInFileDevelopers = "<{*1*}{Din}{Ford}{[1][3]}{[2]}><{*2*}{Xiaoming}{Li}{[2]}{[1]}><{*3*}{Gird}{Long}{[1][2]}{[3]}>";
    private String oldInfoInFileSkills = "";
    private String oldInfoInFileAccounts = "";
    private String oldInfoInFileDevelopers = "";
    private ArrayList<Developer> baseCreatedDevelopers = new ArrayList<>();
    private Developer createdNotUniqueDeveloper = new Developer(2L, "Hof", "Den", new HashSet<>(), new Account(2L));
    private Developer readDeveloper = new Developer(1L, "Din", "Ford",
            Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
            new Account(2L, "Din", AccountStatus.DELETED));
    private Developer updatedDeveloper = new Developer(2L, "Dorf", "Ford",
            Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
            new Account(3L, "Geek", AccountStatus.BANNED));
    private Developer updatedNotExistDeveloper = new Developer(4L, "Sirius", "Black",
            Arrays.stream(new Skill[]{new Skill(1L, "Java")}).collect(Collectors.toSet()),
            new Account(2L, "Din", AccountStatus.DELETED));
    private ArrayList<Developer> getAllDevelopers = new ArrayList<>();
    @Before
    public void clearFileBefore(){
        oldInfoInFileSkills = readFileContent(repoSkill);
        oldInfoInFileAccounts = readFileContent(repoAccount);
        oldInfoInFileDevelopers = readFileContent(repoDeveloper);
        fillFile(repoSkill, newInfoInFileSkills);
        fillFile(repoAccount, newInfoInFileAccounts);
        fillFile(repoDeveloper, "");
    }
    @After
    public void backOldInfo(){
        fillFile(repoSkill, oldInfoInFileSkills);
        fillFile(repoAccount, oldInfoInFileAccounts);
        fillFile(repoDeveloper, oldInfoInFileDevelopers);
    }
    @Test
    public void shouldCreate() {
        Collections.addAll(baseCreatedDevelopers, new Developer(0L, "Din", "Ford",
                        Arrays.stream(new Skill[]{new Skill(1L), new Skill(3L)}).collect(Collectors.toSet()),
                        new Account(2L)),
                new Developer(0L, "Xiaoming", "Li",
                        Arrays.stream(new Skill[]{new Skill(2L)}).collect(Collectors.toSet()),
                        new Account(1L)),
                new Developer(0L, "Gird", "Long",
                        Arrays.stream(new Skill[]{new Skill(1L), new Skill(2L)}).collect(Collectors.toSet()),
                        new Account(3L)));
        String exceptionStr = "";
        try {
            for (Developer developer : baseCreatedDevelopers) {
                testedDeveloperRepo.create(developer);
            }
            testedDeveloperRepo.create(createdNotUniqueDeveloper);
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFileDevelopers, readFileContent(repoDeveloper));
        assertEquals(notUniquePrimaryKeyExceptionStr.replace("#", "Creating"), exceptionStr);
    }
    @Test
    public void shouldGetById() {
        fillFile(repoDeveloper, newInfoInFileDevelopers);
        String exceptionStr = "";
        try {
            assertEquals(readDeveloper, testedDeveloperRepo.getById(1L));
            testedDeveloperRepo.getById(4L);
        } catch (InvalidRepoFileException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            exceptionStr = e.toString();
        }
        assertEquals(noSuchEntryExceptionStr.replace("#", "Reading"), exceptionStr);
    }

    @Test
    public void shouldUpdate() {
        fillFile(repoDeveloper, newInfoInFileDevelopers);
        String exceptionStr = "";
        try {
            testedDeveloperRepo.update(updatedDeveloper);
            testedDeveloperRepo.update(updatedNotExistDeveloper);
        } catch (InvalidRepoFileException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFileDevelopers.replace("{*2*}{Xiaoming}{Li}{[2]}{[1]}", "{*2*}{Dorf}{Ford}{[1][3]}{[3]}"), readFileContent(repoDeveloper));
        assertEquals(noSuchEntryExceptionStr.replace("#", "Updating"), exceptionStr);
    }
    @Test
    public void shouldDelete() {
        fillFile(repoDeveloper, newInfoInFileDevelopers);
        String exceptionStr = "";
        try {
            testedDeveloperRepo.delete(2L);
            testedDeveloperRepo.delete(4L);
        } catch (InvalidRepoFileException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFileDevelopers.replace("<{*2*}{Xiaoming}{Li}{[2]}{[1]}>", ""), readFileContent(repoDeveloper));
        assertEquals(noSuchEntryExceptionStr.replace("#", "Deleting"), exceptionStr);
    }

    @Test
    public void shouldGetAll() {
        fillFile(repoDeveloper, newInfoInFileDevelopers);
        Collections.addAll(getAllDevelopers, new Developer(1L, "Din", "Ford",
                        Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
                        new Account(2L, "Din", AccountStatus.DELETED)),
                new Developer(2L, "Xiaoming", "Li",
                        Arrays.stream(new Skill[]{new Skill(2L, "C#")}).collect(Collectors.toSet()),
                        new Account(1L, "LiXiao", AccountStatus.ACTIVE)),
                new Developer(3L, "Gird", "Long",
                        Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(2L, "C#")}).collect(Collectors.toSet()),
                        new Account(3L, "Geek", AccountStatus.BANNED)));
        try {
            assertEquals(getAllDevelopers, testedDeveloperRepo.getAll());
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
    private void fillFile(File repo, String infoToWrite){
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.write(infoToWrite);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private String readFileContent(File repo){
        String content = "";
        try (FileReader fr = new FileReader(repo)){
            int c;
            while ((c=fr.read()) != -1) content+=(char) c;
        } catch (IOException e) { e.printStackTrace(); }
        return content;
    }
}