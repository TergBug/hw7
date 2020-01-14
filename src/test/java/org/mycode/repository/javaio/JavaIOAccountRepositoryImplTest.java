package org.mycode.repository.javaio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class JavaIOAccountRepositoryImplTest {
    private JavaIOAccountRepositoryImpl testedAccRepo = new JavaIOAccountRepositoryImpl();
    private File repo = new File("./src/main/resources/filestxt/accounts.txt");
    private String noSuchEntryExceptionStr = "org.mycode.exceptions.NoSuchEntryException: # of entry is failed";
    private String notUniquePrimaryKeyExceptionStr = "org.mycode.exceptions.NotUniquePrimaryKeyException: # of entry is failed";
    private String newInfoInFile = "<{*1*}{LiXiao}{ACTIVE}><{*2*}{Din}{DELETED}><{*3*}{Geek}{BANNED}>";
    private String oldInfoInFile = "";
    private ArrayList<Account> baseCreatedAccounts = new ArrayList<>();
    private Account createdNotUniqueAccount = new Account(2L, "Hof", AccountStatus.ACTIVE);
    private Account readAccount = new Account(1L, "LiXiao", AccountStatus.ACTIVE);
    private Account updatedAccount = new Account(2L, "Dorfling", AccountStatus.BANNED);
    private Account updatedNotExistAccount = new Account(4L, "Potter", AccountStatus.ACTIVE);
    private ArrayList<Account> getAllAccounts = new ArrayList<>();
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
        Collections.addAll(baseCreatedAccounts, new Account(0L, "LiXiao", AccountStatus.ACTIVE),
                new Account(0L, "Din", AccountStatus.DELETED),
                new Account(0L, "Geek", AccountStatus.BANNED));
        String exceptionStr = "";
        try {
            for (Account account : baseCreatedAccounts) {
                testedAccRepo.create(account);
            }
            testedAccRepo.create(createdNotUniqueAccount);
        } catch (RepoStorageException | NotUniquePrimaryKeyException e) {
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
            assertEquals(readAccount, testedAccRepo.getById(1L));
            testedAccRepo.getById(4L);
        } catch (RepoStorageException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            exceptionStr = e.toString();
        }
        assertEquals(noSuchEntryExceptionStr.replace("#", "Reading"), exceptionStr);
    }
    @Test
    public void shouldUpdate() {
        fillFile(newInfoInFile);
        String exceptionStr = "";
        try {
            testedAccRepo.update(updatedAccount);
            testedAccRepo.update(updatedNotExistAccount);
        } catch (RepoStorageException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFile.replace("{*2*}{Din}{DELETED}", "{*2*}{Dorfling}{BANNED}"), readFileContent());
        assertEquals(noSuchEntryExceptionStr.replace("#", "Updating"), exceptionStr);
    }
    @Test
    public void shouldDelete() {
        fillFile(newInfoInFile);
        String exceptionStr = "";
        try {
            testedAccRepo.delete(2L);
            testedAccRepo.delete(4L);
        } catch (RepoStorageException | NoSuchEntryException e) {
            exceptionStr = e.toString();
        }
        assertEquals(newInfoInFile.replace("<{*2*}{Din}{DELETED}>", ""), readFileContent());
        assertEquals(noSuchEntryExceptionStr.replace("#", "Deleting"), exceptionStr);
    }
    @Test
    public void shouldGetAll() {
        fillFile(newInfoInFile);
        Collections.addAll(getAllAccounts, new Account(1L, "LiXiao", AccountStatus.ACTIVE),
                new Account(2L, "Din", AccountStatus.DELETED),
                new Account(3L, "Geek", AccountStatus.BANNED));
        try {
            assertEquals(getAllAccounts, testedAccRepo.getAll());
        } catch (RepoStorageException e) {
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