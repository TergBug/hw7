package org.mycode.repository.jdbc;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSkillRepositoryImpl implements SkillRepository {
    private final String JDBC_DRIVER;
    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASSWORD;
    private final String LINK_TO_SQL_SCRIPT = "./src/main/resources/db/initDB.sql";
    private Connection connection;
    public JDBCSkillRepositoryImpl() throws RepoStorageException {
        this.JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        this.DB_URL = "jdbc:mysql://localhost/developercrud?serverTimezone=UTC";
        this.DB_USER = "user";
        this.DB_PASSWORD = "User-192837465";
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            if(e instanceof SQLException){
                throw new RepoStorageException("Cannot connect to SQL DB");
            }
        }
        validateDatabase();
    }
    private void validateDatabase(){
        try (Statement statement = connection.createStatement()){
            String insertQuery = "show tables;";
            ResultSet resultSet = statement.executeQuery(insertQuery);
            int checkIndex = 0;
            while (resultSet.next()){
                if(resultSet.getString(1).toLowerCase().matches("(accounts)" +
                        "|(skills)" +
                        "|(developers)" +
                        "|(developer_skill)")){
                    checkIndex++;
                }
            }
            if(checkIndex!=4){
                ScriptRunner sqlRunner = new ScriptRunner(connection);
                sqlRunner.runScript(new FileReader(LINK_TO_SQL_SCRIPT));
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(Skill model) throws RepoStorageException, NotUniquePrimaryKeyException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String insertQuery = "insert into skills(name) values ('"+model.getName()+"');";
            statement.execute(insertQuery);
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in creation");
        }
    }
    @Override
    public Skill getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (Statement statement = connection.createStatement()){
            String selectQuery = "select * from skills where id='"+readID+"';";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.absolute(2)){
                throw new NotUniquePrimaryKeyException("Reading from DB is failed");
            }
            resultSet.first();
            return new Skill(resultSet.getLong(1), resultSet.getString(2));
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Skill updatedModel) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (Statement statement = connection.createStatement()){
            String updateQuery = "update skills " +
                    "set name='"+updatedModel.getName()+"' " +
                    "where id='"+updatedModel.getId()+"';";
            if(statement.executeUpdate(updateQuery)<1){
                throw new NoSuchEntryException("Updating in DB is failed");
            }
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (Statement statement = connection.createStatement()){
            String deleteQuery = "delete from skills where id='"+deletedEntry+"';";
            if(statement.executeUpdate(deleteQuery)<1){
                throw new NoSuchEntryException("Deleting in DB is failed");
            }
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public List<Skill> getAll() throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (Statement statement = connection.createStatement()){
            String selectQuery = "select * from skills;";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            ArrayList<Skill> skills = new ArrayList<>();
            while (resultSet.next()){
                skills.add(new Skill(resultSet.getLong(1), resultSet.getString(2)));
            }
            return skills;
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
