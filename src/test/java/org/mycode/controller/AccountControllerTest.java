package org.mycode.controller;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.service.AccountService;
import org.mycode.service.TypeOfStorage;
import org.mycode.testutil.TestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {
    @InjectMocks
    private AccountController testedAccountController = AccountController.getInstance();
    @Mock
    private AccountService service;
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private String createRequest = "c|0|Root|ACTIVE";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|Dorf|BANNED";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String changeStorageToDBRequest = "db";
    private String changeStorageToFileRequest = "f";
    private String wrongRequest = "p";
    private Account createAccount = new Account(0L, "Root", AccountStatus.ACTIVE);
    private Account updateAccount = new Account(2L, "Dorf", AccountStatus.BANNED);
    public AccountControllerTest() throws RepoStorageException { }
    @BeforeClass
    public static void connect(){
        TestUtils.switchConfigToTestMode();
    }
    @AfterClass
    public static void backProperty(){
        TestUtils.switchConfigToWorkMode();
    }
    @Test
    public void shouldComputeRequest() {
        String exceptionStr = "";
        try {
            testedAccountController.request(createRequest);
            verify(service, times(1)).create(createAccount);
            testedAccountController.request(readRequest);
            verify(service, times(1)).getById(1L);
            testedAccountController.request(updateRequest);
            verify(service, times(1)).update(updateAccount);
            testedAccountController.request(deleteRequest);
            verify(service, times(1)).delete(4L);
            testedAccountController.request(getAllRequest);
            verify(service, times(1)).getAll();
            testedAccountController.request(changeStorageToDBRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.DATABASE);
            testedAccountController.request(changeStorageToFileRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.FILES);
            testedAccountController.request(wrongRequest);
        } catch (Exception e) {
            if(e instanceof IncorrectRequestException) {
                exceptionStr = e.toString();
            }
        }
        assertEquals(incorrectRequestExceptionStr, exceptionStr);
    }
}