package org.mycode.repository.javaio;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class JavaIOSkillRepositoryImplTest {
    private JavaIOSkillRepositoryImpl testedSkillRepo = new JavaIOSkillRepositoryImpl();
    private File repo = new File("./src/main/resources/skills.txt");
    private String noSuchEntryExceptionStr = "org.mycode.exceptions.NoSuchEntryException: # of entry is failed";
    private String notUniquePrimaryKeyExceptionStr = "org.mycode.exceptions.NotUniquePrimaryKeyException: # of entry is failed";
    private String newInfoInFile = "<{*1*}{Java}><{*2*}{C#}><{*3*}{JDBC}>";
    private String oldInfoInFile = "";
    private ArrayList<Skill> baseCreatedSkills = new ArrayList<>();
    private Skill createdNotUniqueSkill = new Skill(2L, "C++");
    private Skill readSkill = new Skill(1L, "Java");
    private Skill updatedSkill = new Skill(2L, "DB");
    private Skill updatedNotExistSkill = new Skill(4L, "DB");
    private ArrayList<Skill> getAllSkills = new ArrayList<>();
    @Before
    public void clearFileBefore(){
        oldInfoInFile = readFileContent();
        fillFile("");
    }
    @After
    public void backOldInfo(){
        fillFile(oldInfoInFile);
    }
    @Test
    public void shouldCreate() {
        Collections.addAll(baseCreatedSkills, new Skill(0L, "Java"),
                new Skill(0L, "C#"),
                new Skill(0L, "JDBC"));
        String exceptionStr = "";
        try {
            for (Skill skill : baseCreatedSkills) {
                testedSkillRepo.create(skill);
            }
            testedSkillRepo.create(createdNotUniqueSkill);
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFile, readFileContent());
        assertEquals(notUniquePrimaryKeyExceptionStr.replace("#", "Creating"), exceptionStr);
    }
    @Test
    public void shouldRead() {
        fillFile(newInfoInFile);
        String exceptionStr = "";
        try {
            assertEquals(readSkill, testedSkillRepo.getById(1L));
            testedSkillRepo.getById(4L);
        } catch (InvalidRepoFileException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            exceptionStr = e.toString();
        }
        assertEquals(noSuchEntryExceptionStr.replace("#", "Reading"), exceptionStr);
    }
    @Test
    public void shouldUpdate() {
        fillFile(newInfoInFile);
        String exceptionStr = "";
        try {
            testedSkillRepo.update(updatedSkill);
            testedSkillRepo.update(updatedNotExistSkill);
        } catch (InvalidRepoFileException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFile.replace("{*2*}{C#}", "{*2*}{DB}"), readFileContent());
        assertEquals(noSuchEntryExceptionStr.replace("#", "Updating"), exceptionStr);
    }
    @Test
    public void shouldDelete() {
        fillFile(newInfoInFile);
        String exceptionStr = "";
        try {
            testedSkillRepo.delete(2L);
            testedSkillRepo.delete(4L);
        } catch (InvalidRepoFileException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFile.replace("<{*2*}{C#}>", ""), readFileContent());
        assertEquals(noSuchEntryExceptionStr.replace("#", "Deleting"), exceptionStr);
    }
    @Test
    public void shouldGetAll() {
        fillFile(newInfoInFile);
        Collections.addAll(getAllSkills, new Skill(1L, "Java"),
                new Skill(2L, "C#"),
                new Skill(3L, "JDBC"));
        try {
            assertEquals(getAllSkills, testedSkillRepo.getAll());
        } catch (InvalidRepoFileException e) {
            e.printStackTrace();
        }
    }
    private void fillFile(String infoToWrite){
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.write(infoToWrite);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private String readFileContent(){
        String content = "";
        try (FileReader fr = new FileReader(repo)){
            int c;
            while ((c=fr.read()) != -1) content+=(char) c;
        } catch (IOException e) { e.printStackTrace(); }
        return content;
    }
}