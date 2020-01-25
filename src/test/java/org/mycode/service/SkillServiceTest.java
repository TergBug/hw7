package org.mycode.service;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.repository.javaio.JavaIOSkillRepositoryImpl;
import org.mycode.repository.jdbc.JDBCSkillRepositoryImpl;
import org.mycode.testutil.TestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SkillServiceTest {
    private SkillService testedSkillService;
    private SkillRepository currentRepo;
    private Skill createSkill = new Skill(5L, "Java");
    private Skill updateSkill = new Skill(5L, "JDBC");
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
        currentRepo = mock(SkillRepository.class);
        try {
            testedSkillService = new SkillService(currentRepo);
        } catch (RepoStorageException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeCreateInRepo() {
        try {
            testedSkillService.create(createSkill);
            verify(currentRepo, times(1)).create(createSkill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeGetByIdInRepo() {
        try {
            testedSkillService.getById(1L);
            verify(currentRepo, times(1)).getById(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeUpdateInRepo() {
        try {
            testedSkillService.update(updateSkill);
            verify(currentRepo, times(1)).update(updateSkill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeDeleteInRepo() {
        try {
            testedSkillService.delete(2L);
            verify(currentRepo, times(1)).delete(2L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldInvokeGetAllInRepo() {
        try {
            testedSkillService.getAll();
            verify(currentRepo, times(1)).getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldChangeStorage() {
        try {
            testedSkillService = new SkillService();
        } catch (RepoStorageException e) {
            e.printStackTrace();
        }
        testedSkillService.changeStorage(TypeOfStorage.DATABASE);
        assertTrue(testedSkillService.getCurrentRepo() instanceof JDBCSkillRepositoryImpl);
        testedSkillService.changeStorage(TypeOfStorage.FILES);
        assertTrue(testedSkillService.getCurrentRepo() instanceof JavaIOSkillRepositoryImpl);
    }
}