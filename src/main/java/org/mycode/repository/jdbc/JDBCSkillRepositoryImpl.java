package org.mycode.repository.jdbc;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.mapping.JDBCSkillMapper;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.util.JDBCConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSkillRepositoryImpl implements SkillRepository {
    private static final Logger log = Logger.getLogger(JDBCSkillRepositoryImpl.class);
    private final String INSERT_QUERY = "insert into skills(name) values (?);";
    private final String SELECT_QUERY = "select * from skills where id=?;";
    private final String UPDATE_QUERY = "update skills set name=? where id=?;";
    private final String DELETE_QUERY = "delete from skills where id=?;";
    private final String SELECT_ALL_QUERY = "select * from skills;";
    private Connection connection;
    public JDBCSkillRepositoryImpl() throws RepoStorageException {
        try {
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            log.error("Cannot connect to SQL DB", e);
            throw new RepoStorageException("Cannot connect to SQL DB");
        }
    }
    @Override
    public void create(Skill model) throws RepoStorageException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            statement.setString(1, model.getName());
            statement.execute();
            log.debug("Create entry(DB): "+model);
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in creation", e);
            throw new RepoStorageException("Wrong SQL query to DB in creation");
        }
    }
    @Override
    public Skill getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            statement.setLong(1, readID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.absolute(2)){
                log.warn("Not unique primary key: "+readID);
                throw new NotUniquePrimaryKeyException("Reading from DB is failed");
            }
            resultSet.first();
            Skill skill = new JDBCSkillMapper().map(resultSet, readID);
            log.debug("Read entry(DB) with ID: "+readID);
            return skill;
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Skill updatedModel) throws RepoStorageException, NoSuchEntryException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            statement.setString(1, updatedModel.getName());
            statement.setLong(2, updatedModel.getId());
            if(statement.executeUpdate()<1){
                log.warn("No such entry: "+updatedModel);
                throw new NoSuchEntryException("Updating in DB is failed");
            }
            log.debug("Update entry(DB): "+updatedModel);
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in updating", e);
            throw new RepoStorageException("Wrong SQL query to DB in updating");
        }
    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            statement.setLong(1, deletedEntry);
            if(statement.executeUpdate()<1){
                log.warn("No such entry with ID: "+deletedEntry);
                throw new NoSuchEntryException("Deleting in DB is failed");
            }
            log.debug("Delete entry(DB) with ID: "+deletedEntry);
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in deleting", e);
            throw new RepoStorageException("Wrong SQL query to DB in deleting");
        }
    }
    @Override
    public List<Skill> getAll() throws RepoStorageException, NoSuchEntryException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Skill> skills = new ArrayList<>();
            JDBCSkillMapper mapper = new JDBCSkillMapper();
            while (resultSet.next()){
                skills.add(mapper.map(resultSet, resultSet.getLong(1)));
            }
            log.debug("Read all entries(DB)");
            return skills;
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
