package org.mycode.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class SkillControllerTest {
    private SkillController testedSkillController = SkillController.getInstance();
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private File repo = new File("./src/main/resources/filestxt/skills.txt");
    private String newInfoInFile = "<{*1*}{Java}><{*2*}{C#}><{*3*}{JDBC}>";
    private String oldInfoInFile = "";
    private String createRequest = "c|0|Python";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|DB";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String wrongRequest = "p";
    private Skill readSkill = new Skill(1L, "Java");
    private ArrayList<Skill> allSkills = new ArrayList<>();
    public SkillControllerTest() throws RepoStorageException { }
    @Before
    public void loadFileBefore(){
        Collections.addAll(allSkills, new Skill(1L, "Java"),
                new Skill(2L, "C#"),
                new Skill(3L, "JDBC"));
        oldInfoInFile = readFileContent();
        fillFile(newInfoInFile);
    }
    @After
    public void backOldInfo(){
        fillFile(oldInfoInFile);
    }
    @Test
    public void shouldComputeRequest() {
        String exceptionStr = "";
        try {
            assertEquals(allSkills, testedSkillController.request(getAllRequest));
            assertEquals(0, testedSkillController.request(createRequest).size());
            assertEquals(readSkill, testedSkillController.request(readRequest).get(0));
            assertEquals(0, testedSkillController.request(updateRequest).size());
            assertEquals(0, testedSkillController.request(deleteRequest).size());
            testedSkillController.request(wrongRequest);
        } catch (IncorrectRequestException e) {
            exceptionStr = e.toString();
        }
        assertEquals(incorrectRequestExceptionStr, exceptionStr);
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