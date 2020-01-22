package org.mycode.repository.jdbc;

import static org.junit.Assert.*;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.mapping.JDBCSkillMapper;
import org.mycode.model.Skill;
import org.mycode.util.JDBCConnectionUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class JDBCSkillRepositoryImplTest {
    private static final Logger log = Logger.getLogger(JDBCSkillRepositoryImplTest.class);
    private static final String LINK_TO_PROPERTIES = "./src/main/resources/config.properties";
    private final String LINK_TO_INIT_SCRIPT = "./src/test/resources/db/initDB.sql";
    private final String LINK_TO_POP_SCRIPT = "./src/test/resources/db/populateDB.sql";
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String JDBC_URL = "jdbc:h2:~/test";
    private static final String JDBC_USERNAME = "sa";
    private static final String JDBC_PASSWORD = "";
    private static JDBCSkillRepositoryImpl testedRepo;
    private static Connection connection;
    private static String oldContent = "";
    private static Properties newProperties = new Properties();
    private String selectQueryForCreate = "select * from skills group by id having max(id);";
    private String selectQuery = "select * from skills where id=?;";
    private Skill createdSkill = new Skill(5L, "HTML");
    private Skill readSkill = new Skill(2L, "C#");
    private Skill updatedSkill = new Skill(1L, "JavaScript");
    private List<Skill> allSkill = new ArrayList<>();
    @BeforeClass
    public static void connect() throws RepoStorageException {
        try{
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (BufferedReader fr = new BufferedReader(new FileReader(LINK_TO_PROPERTIES))){
            int c;
            while ((c=fr.read())!=-1){
                oldContent +=(char)c;
            }
            newProperties.load(new ByteArrayInputStream(oldContent.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        newProperties.replace("jdbc.driver", JDBC_DRIVER);
        newProperties.replace("jdbc.url", JDBC_URL);
        newProperties.replace("jdbc.user", JDBC_USERNAME);
        newProperties.replace("jdbc.password", JDBC_PASSWORD);
        try(FileWriter fw = new FileWriter(LINK_TO_PROPERTIES, false)){
            newProperties.store(fw, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        testedRepo = new JDBCSkillRepositoryImpl();
    }
    @Before
    public void setupProperty(){
        try(FileWriter fw = new FileWriter(LINK_TO_PROPERTIES, false)){
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new FileReader(LINK_TO_INIT_SCRIPT));
            scriptRunner.runScript(new FileReader(LINK_TO_POP_SCRIPT));
            newProperties.store(fw, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @After
    public void backProperty(){
        try(FileWriter fw = new FileWriter(LINK_TO_PROPERTIES, false)){
            fw.write(oldContent);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldCreate() {
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.create(createdSkill);
            ResultSet resultSet = statement.executeQuery(selectQueryForCreate);
            assertEquals(createdSkill, new JDBCSkillMapper().map(resultSet, 5L));
            log.debug("Create");
        } catch (RepoStorageException | SQLException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldGetById() {
        try {
            assertEquals(readSkill, testedRepo.getById(2L));
            log.debug("Read");
        } catch (RepoStorageException | NoSuchEntryException | NotUniquePrimaryKeyException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldUpdate() {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            testedRepo.update(updatedSkill);
            statement.setLong(1, 1);
            ResultSet resultSet = statement.executeQuery();
            assertEquals(updatedSkill, new JDBCSkillMapper().map(resultSet, 1L));
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
            Collections.addAll(allSkill, new Skill(1L, "Java"),
                    new Skill(2L, "C#"),
                    new Skill(3L, "JDBC"),
                    new Skill(4L, "JSON"));
            assertEquals(allSkill, testedRepo.getAll());
            log.debug("GetAll");
        } catch (RepoStorageException | NoSuchEntryException e) {
            e.printStackTrace();
        }
    }
}