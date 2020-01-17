package org.mycode.repository.jdbc;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.mapping.JDBCSkillMapper;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSkillRepositoryImpl implements SkillRepository {
    private Connection connection;
    public JDBCSkillRepositoryImpl() throws RepoStorageException {
        JDBCUtils.makeConnectionToDB();
        connection = JDBCUtils.getConnection();
    }
    @Override
    public void create(Skill model) throws RepoStorageException {
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
            return new JDBCSkillMapper().map(resultSet, readID);
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Skill updatedModel) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String updateQuery = "update skills " +
                    "set name='"+updatedModel.getName()+"' " +
                    "where id='"+updatedModel.getId()+"';";
            if(statement.executeUpdate(updateQuery)<1){
                throw new NoSuchEntryException("Updating in DB is failed");
            }
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in updating");
        }
    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String deleteQuery = "delete from skills where id='"+deletedEntry+"';";
            if(statement.executeUpdate(deleteQuery)<1){
                throw new NoSuchEntryException("Deleting in DB is failed");
            }
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in deleting");
        }
    }
    @Override
    public List<Skill> getAll() throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String selectQuery = "select * from skills;";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            ArrayList<Skill> skills = new ArrayList<>();
            JDBCSkillMapper mapper = new JDBCSkillMapper();
            while (resultSet.next()){
                skills.add(mapper.map(resultSet, resultSet.getLong(1)));
            }
            return skills;
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
