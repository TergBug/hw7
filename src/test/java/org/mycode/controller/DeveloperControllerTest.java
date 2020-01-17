package org.mycode.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.model.Developer;
import org.mycode.model.Skill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DeveloperControllerTest {
    private DeveloperController testedDeveloperController = DeveloperController.getInstance();
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private File repoSkill = new File("./src/main/resources/filestxt/skills.txt");
    private File repoAccount = new File("./src/main/resources/filestxt/accounts.txt");
    private File repoDeveloper = new File("./src/main/resources/filestxt/developers.txt");
    private String newInfoInFileSkills = "<{*1*}{Java}><{*2*}{C#}><{*3*}{JDBC}>";
    private String newInfoInFileAccounts = "<{*1*}{LiXiao}{ACTIVE}><{*2*}{Din}{DELETED}><{*3*}{Geek}{BANNED}>";
    private String newInfoInFileDevelopers = "<{*1*}{Din}{Ford}{[1][3]}{[2]}><{*2*}{Xiaoming}{Li}{[2]}{[1]}><{*3*}{Gird}{Long}{[1][2]}{[3]}>";
    private String oldInfoInFileSkills = "";
    private String oldInfoInFileAccounts = "";
    private String oldInfoInFileDevelopers = "";
    private String createRequest = "c|0|Fred|Lord|2,3|1";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|Dorf|Ford|1,3|3";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String wrongRequest = "p";
    private Developer readDeveloper = new Developer(1L, "Din", "Ford",
            Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
            new Account(2L, "Din", AccountStatus.DELETED));
    private ArrayList<Developer> allDevelopers = new ArrayList<>();
    public DeveloperControllerTest() throws RepoStorageException { }
    @Before
    public void loadFileBefore(){
        Collections.addAll(allDevelopers, new Developer(1L, "Din", "Ford",
                        Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
                        new Account(2L, "Din", AccountStatus.DELETED)),
                new Developer(2L, "Xiaoming", "Li",
                        Arrays.stream(new Skill[]{new Skill(2L, "C#")}).collect(Collectors.toSet()),
                        new Account(1L, "LiXiao", AccountStatus.ACTIVE)),
                new Developer(3L, "Gird", "Long",
                        Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(2L, "C#")}).collect(Collectors.toSet()),
                        new Account(3L, "Geek", AccountStatus.BANNED)));
        oldInfoInFileSkills = readFileContent(repoSkill);
        oldInfoInFileAccounts = readFileContent(repoAccount);
        oldInfoInFileDevelopers = readFileContent(repoDeveloper);
        fillFile(repoSkill, newInfoInFileSkills);
        fillFile(repoAccount, newInfoInFileAccounts);
        fillFile(repoDeveloper, newInfoInFileDevelopers);
    }
    @After
    public void backOldInfo(){
        fillFile(repoSkill, oldInfoInFileSkills);
        fillFile(repoAccount, oldInfoInFileAccounts);
        fillFile(repoDeveloper, oldInfoInFileDevelopers);
    }
    @Test
    public void shouldComputeRequest() {
        String exceptionStr = "";
        try {
            assertEquals(allDevelopers, testedDeveloperController.request(getAllRequest));
            assertEquals(0, testedDeveloperController.request(createRequest).size());
            assertEquals(readDeveloper, testedDeveloperController.request(readRequest).get(0));
            assertEquals(0, testedDeveloperController.request(updateRequest).size());
            assertEquals(0, testedDeveloperController.request(deleteRequest).size());
            testedDeveloperController.request(wrongRequest);
        } catch (IncorrectRequestException e) {
            exceptionStr = e.toString();
        }
        assertEquals(incorrectRequestExceptionStr, exceptionStr);
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