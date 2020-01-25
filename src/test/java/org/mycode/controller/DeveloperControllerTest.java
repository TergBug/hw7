package org.mycode.controller;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.service.DeveloperService;
import org.mycode.service.TypeOfStorage;
import org.mycode.testutil.TestUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeveloperControllerTest {
    @InjectMocks
    private DeveloperController testedDeveloperController = DeveloperController.getInstance();
    @Mock
    private DeveloperService service;
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private String createRequest = "c|0|Fred|Lord|2,3|1";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|Dorf|Ford|1,3|3";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String changeStorageToDBRequest = "db";
    private String changeStorageToFileRequest = "f";
    private String wrongRequest = "p";
    private Developer createDeveloper = new Developer(0L, "Fred", "Lord",
            Arrays.stream(new Skill[]{new Skill(2L), new Skill(3L)}).collect(Collectors.toSet()),
            new Account(1L));
    private Developer updateDeveloper = new Developer(2L, "Dorf", "Ford",
            Arrays.stream(new Skill[]{new Skill(1L), new Skill(3L)}).collect(Collectors.toSet()),
            new Account(3L));
    public DeveloperControllerTest() throws RepoStorageException { }
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
            testedDeveloperController.request(createRequest);
            verify(service, times(1)).create(createDeveloper);
            testedDeveloperController.request(readRequest);
            verify(service, times(1)).getById(1L);
            testedDeveloperController.request(updateRequest);
            verify(service, times(1)).update(updateDeveloper);
            testedDeveloperController.request(deleteRequest);
            verify(service, times(1)).delete(4L);
            testedDeveloperController.request(getAllRequest);
            verify(service, times(1)).getAll();
            testedDeveloperController.request(changeStorageToDBRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.DATABASE);
            testedDeveloperController.request(changeStorageToFileRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.FILES);
            testedDeveloperController.request(wrongRequest);
        } catch (Exception e) {
            if(e instanceof IncorrectRequestException) {
                exceptionStr = e.toString();
            }
        }
        assertEquals(incorrectRequestExceptionStr, exceptionStr);
    }
}