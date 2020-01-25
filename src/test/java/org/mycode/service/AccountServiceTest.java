package org.mycode.service;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.repository.AccountRepository;
import org.mycode.repository.javaio.JavaIOAccountRepositoryImpl;
import org.mycode.repository.jdbc.JDBCAccountRepositoryImpl;
import org.mycode.testutil.TestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    private AccountService testedAccountService;
    private AccountRepository currentRepo;
    private Account createAccount = new Account(5L, "Jog", AccountStatus.ACTIVE);
    private Account updateAccount = new Account(5L, "Pof", AccountStatus.BANNED);
    @BeforeClass
    public static void connect(){
        TestUtils.switchConfigToTestMode();
    }
    @AfterClass
    public static void backProperty(){
        TestUtils.switchConfigToWorkMode();
    }
    @Before
    public void injectMock(){
        currentRepo = mock(AccountRepository.class);
        try {
            testedAccountService = new AccountService(currentRepo);
        } catch (RepoStorageException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeCreateInRepo() {
        try {
            testedAccountService.create(createAccount);
            verify(currentRepo, times(1)).create(createAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeGetByIdInRepo() {
        try {
            testedAccountService.getById(1L);
            verify(currentRepo, times(1)).getById(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeUpdateInRepo() {
        try {
            testedAccountService.update(updateAccount);
            verify(currentRepo, times(1)).update(updateAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeDeleteInRepo() {
        try {
            testedAccountService.delete(2L);
            verify(currentRepo, times(1)).delete(2L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeGetAllInRepo() {
        try {
            testedAccountService.getAll();
            verify(currentRepo, times(1)).getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldChangeStorage() {
        try {
            testedAccountService = new AccountService();
        } catch (RepoStorageException e) {
            e.printStackTrace();
        }
        testedAccountService.changeStorage(TypeOfStorage.DATABASE);
        assertTrue(testedAccountService.getCurrentRepo() instanceof JDBCAccountRepositoryImpl);
        testedAccountService.changeStorage(TypeOfStorage.FILES);
        assertTrue(testedAccountService.getCurrentRepo() instanceof JavaIOAccountRepositoryImpl);
    }
}