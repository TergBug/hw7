package org.mycode.controller;

import org.junit.*;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.testutil.TestUtils;
import org.mycode.util.JavaIOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class AccountControllerTest {
    private AccountController testedAccController = AccountController.getInstance();
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private File repo = JavaIOUtils.getAccountRepo();
    private String newInfoInFile = "<{*1*}{LiXiao}{ACTIVE}><{*2*}{Din}{DELETED}><{*3*}{Geek}{BANNED}><{*4*}{Ford}{ACTIVE}>";
    private String oldInfoInFile = "";
    private String createRequest = "c|0|Root|ACTIVE";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|Dorf|BANNED";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String wrongRequest = "p";
    private Account readAccount = new Account(1L, "LiXiao", AccountStatus.ACTIVE);
    private ArrayList<Account> allAccounts = new ArrayList<>();
    public AccountControllerTest() throws RepoStorageException { }
    @BeforeClass
    public static void connect(){
        TestUtils.switchConfigToTestMode();
    }
    @AfterClass
    public static void backProperty(){
        TestUtils.switchConfigToWorkMode();
    }
    @Before
    public void loadFileBefore(){
        Collections.addAll(allAccounts, new Account(1L, "LiXiao", AccountStatus.ACTIVE),
                new Account(2L, "Din", AccountStatus.DELETED),
                new Account(3L, "Geek", AccountStatus.BANNED),
                new Account(4L, "Ford", AccountStatus.ACTIVE));
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
            assertEquals(allAccounts, testedAccController.request(getAllRequest));
            assertEquals(0, testedAccController.request(createRequest).size());
            assertEquals(readAccount, testedAccController.request(readRequest).get(0));
            assertEquals(0, testedAccController.request(updateRequest).size());
            assertEquals(0, testedAccController.request(deleteRequest).size());
            testedAccController.request(wrongRequest);
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