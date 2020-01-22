package org.mycode.repository.jdbc;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;
import org.junit.*;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.mapping.JDBCAccountMapper;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.testutil.TestUtils;
import org.mycode.util.JDBCConnectionUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JDBCAccountRepositoryImplTest {
    private static final Logger log = Logger.getLogger(JDBCAccountRepositoryImplTest.class);
    private static final String LINK_TO_INIT_SCRIPT = "./src/test/resources/db/initDB.sql";
    private static final String LINK_TO_POP_SCRIPT = "./src/test/resources/db/populateDB.sql";
    private static JDBCAccountRepositoryImpl testedRepo;
    private static Connection connection;
    private String selectQueryForCreate = "select * from accounts group by id having max(id);";
    private String selectQuery = "select * from accounts where id=?;";
    private Account createdAccount = new Account(5L, "Lord", AccountStatus.ACTIVE);
    private Account readAccount = new Account(2L, "Din", AccountStatus.DELETED);
    private Account updatedAccount = new Account(1L, "Ming", AccountStatus.BANNED);
    private List<Account> allAccount = new ArrayList<>();
    @BeforeClass
    public static void connect() throws RepoStorageException {
        TestUtils.switchConfigToTestMode();
        try{
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        testedRepo = new JDBCAccountRepositoryImpl();
    }
    @Before
    public void setupProperty(){
        try(FileReader frInit = new FileReader(LINK_TO_INIT_SCRIPT);
            FileReader frPop = new FileReader(LINK_TO_POP_SCRIPT)){
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(frInit);
            scriptRunner.runScript(frPop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @AfterClass
    public static void backProperty(){
        TestUtils.switchConfigToWorkMode();
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldCreate() {
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.create(createdAccount);
            ResultSet resultSet = statement.executeQuery(selectQueryForCreate);
            assertEquals(createdAccount, new JDBCAccountMapper().map(resultSet, 5L));
            log.debug("Create");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            fail();
        }
    }
    @Test
    public void shouldGetById() {
        try {
            assertEquals(readAccount, testedRepo.getById(2L));
            log.debug("Read");
        } catch (RepoStorageException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            fail();
        }
    }
    @Test
    public void shouldUpdate() {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.update(updatedAccount);
            statement.setLong(1, 1);
            ResultSet resultSet = statement.executeQuery();
            assertEquals(updatedAccount, new JDBCAccountMapper().map(resultSet, 1L));
            log.debug("Update");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            fail();
        }
    }
    @Test
    public void shouldDelete() {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.delete(4L);
            statement.setLong(1, 4);
            assertFalse(statement.executeQuery().next());
            log.debug("Delete");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            fail();
        }
    }
    @Test
    public void shouldGetAll() {
        try {
            Collections.addAll(allAccount, new Account(1L, "LiXiao", AccountStatus.ACTIVE),
                    new Account(2L, "Din", AccountStatus.DELETED),
                    new Account(3L, "Geek", AccountStatus.BANNED),
                    new Account(4L, "Ford", AccountStatus.ACTIVE));
            assertEquals(allAccount, testedRepo.getAll());
            log.debug("GetAll");
        } catch (RepoStorageException | NoSuchEntryException e) {
            fail();
        }
    }
}