package org.mycode.repository.jdbc;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.mapping.JDBCDeveloperMapper;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.testutil.TestUtils;
import org.mycode.util.JDBCConnectionUtil;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JDBCDeveloperRepositoryImplTest {
    private static final Logger log = Logger.getLogger(JDBCDeveloperRepositoryImplTest.class);
    private static final String LINK_TO_INIT_SCRIPT = "./src/test/resources/db/initDB.sql";
    private static final String LINK_TO_POP_SCRIPT = "./src/test/resources/db/populateDB.sql";
    private static JDBCDeveloperRepositoryImpl testedRepo;
    private static Connection connection;
    private String selectQueryForCreate = "select max(d.id), d.first_name, d.last_name, s.id, s.name, a.id, a.name, a.status " +
            "from developers d " +
            "left join (" +
            "select ds.developer_id, s.id, s.name " +
            "from developer_skill ds " +
            "inner join skills s " +
            "on ds.skill_id = s.id) s " +
            "on d.id = s.developer_id " +
            "left join accounts a " +
            "on d.account_id = a.id " +
            "group by s.id, d.last_name, d.first_name, s.name, a.id, a.name, a.status;";
    private String selectQuery = "select d.id, d.first_name, d.last_name, s.id, s.name, a.id, a.name, a.status " +
            "from developers d " +
            "left join (" +
            "select ds.developer_id, s.id, s.name " +
            "from developer_skill ds " +
            "inner join skills s " +
            "on ds.skill_id = s.id) s " +
            "on d.id = s.developer_id " +
            "left join accounts a " +
            "on d.account_id = a.id " +
            "where d.id=?;";
    private Developer createdDeveloper = new Developer(5L, "Lord", "Dog",
            Arrays.stream(new Skill[]{new Skill(2L, "C#")}).collect(Collectors.toSet()),
            new Account(3L, "Geek", AccountStatus.BANNED));
    private Developer readDeveloper = new Developer(2L, "Xiaoming", "Li",
            Arrays.stream(new Skill[]{new Skill(2L, "C#")}).collect(Collectors.toSet()),
            new Account(1L, "LiXiao", AccountStatus.ACTIVE));
    private Developer updatedDeveloper = new Developer(1L, "Dinis", "Wong",
            Arrays.stream(new Skill[]{new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
            new Account(2L, "Din", AccountStatus.DELETED));
    private List<Developer> allDeveloper = new ArrayList<>();
    @BeforeClass
    public static void connect() throws RepoStorageException {
        TestUtils.switchConfigToTestMode();
        try{
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        testedRepo = new JDBCDeveloperRepositoryImpl();
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
    @Test
    public void shouldCreate() {
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.create(createdDeveloper);
            ResultSet resultSet = statement.executeQuery(selectQueryForCreate);
            assertEquals(createdDeveloper, new JDBCDeveloperMapper().map(resultSet, 5L));
            log.debug("Create");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldGetById() {
        try {
            assertEquals(readDeveloper, testedRepo.getById(2L));
            log.debug("Read");
        } catch (RepoStorageException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldUpdate() {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.update(updatedDeveloper);
            statement.setLong(1, 1);
            ResultSet resultSet = statement.executeQuery();
            assertEquals(updatedDeveloper, new JDBCDeveloperMapper().map(resultSet, 1L));
            log.debug("Update");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    @Test
    public void shouldGetAll() {
        try {
            Collections.addAll(allDeveloper, new Developer(1L, "Din", "Ford",
                            Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(3L, "JDBC")}).collect(Collectors.toSet()),
                            new Account(2L, "Din", AccountStatus.DELETED)),
                    new Developer(2L, "Xiaoming", "Li",
                            Arrays.stream(new Skill[]{new Skill(2L, "C#")}).collect(Collectors.toSet()),
                            new Account(1L, "LiXiao", AccountStatus.ACTIVE)),
                    new Developer(3L, "Gird", "Long",
                            Arrays.stream(new Skill[]{new Skill(1L, "Java"), new Skill(2L, "C#")}).collect(Collectors.toSet()),
                            new Account(3L, "Geek", AccountStatus.BANNED)),
                    new Developer(4L, "Gordon", "Fong",
                            new HashSet<>(),
                            new Account(1L, "LiXiao", AccountStatus.ACTIVE)));
            assertEquals(allDeveloper, testedRepo.getAll());
            log.debug("GetAll");
        } catch (RepoStorageException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
}