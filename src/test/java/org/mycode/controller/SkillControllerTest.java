package org.mycode.controller;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;
import org.mycode.service.SkillService;
import org.mycode.service.TypeOfStorage;
import org.mycode.testutil.TestUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController testedSkillController = SkillController.getInstance();
    @Mock
    private SkillService service;
    private String incorrectRequestExceptionStr = "org.mycode.exceptions.IncorrectRequestException";
    private String createRequest = "c|0|Python";
    private String readRequest = "r|1";
    private String updateRequest = "u|2|DB";
    private String deleteRequest = "d|4";
    private String getAllRequest = "g";
    private String changeStorageToDBRequest = "db";
    private String changeStorageToFileRequest = "f";
    private String wrongRequest = "p";
    private Skill createSkill = new Skill(0L, "Python");
    private Skill updateSkill = new Skill(2L, "DB");
    public SkillControllerTest() throws RepoStorageException { }
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
            testedSkillController.request(createRequest);
            verify(service, times(1)).create(createSkill);
            testedSkillController.request(readRequest);
            verify(service, times(1)).getById(1L);
            testedSkillController.request(updateRequest);
            verify(service, times(1)).update(updateSkill);
            testedSkillController.request(deleteRequest);
            verify(service, times(1)).delete(4L);
            testedSkillController.request(getAllRequest);
            verify(service, times(1)).getAll();
            testedSkillController.request(changeStorageToDBRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.DATABASE);
            testedSkillController.request(changeStorageToFileRequest);
            verify(service, times(1)).changeStorage(TypeOfStorage.FILES);
            testedSkillController.request(wrongRequest);
        } catch (Exception e) {
            if(e instanceof IncorrectRequestException) {
                exceptionStr = e.toString();
            }
        }
        assertEquals(incorrectRequestExceptionStr, exceptionStr);
    }
}