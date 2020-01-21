package org.mycode.repository.jdbc;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.mapping.JDBCDeveloperMapper;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.repository.DeveloperRepository;
import org.mycode.util.JDBCConnectionUtil;

import java.sql.*;
import java.util.*;

public class JDBCDeveloperRepositoryImpl implements DeveloperRepository {
    private static final Logger log = Logger.getLogger(JDBCDeveloperRepositoryImpl.class);
    private final String INSERT_QUERY = "insert into developers(first_name, last_name, account_id) values (?, ?, ?);";
    private final String SELECT_QUERY = "select * from accounts where id=?;";
    private final String UPDATE_QUERY = "update accounts set name=?, status=? where id=?;";
    private final String DELETE_QUERY = "delete from accounts where id=?;";
    private final String SELECT_ALL_QUERY = "select * from accounts;";
    private Connection connection;
    public JDBCDeveloperRepositoryImpl() throws RepoStorageException {
        try {
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            log.error("Cannot connect to SQL DB", e);
            throw new RepoStorageException("Cannot connect to SQL DB");
        }
    }
    @Override
    public void create(Developer model) throws RepoStorageException {
        try (Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            String insertToDevelopersTableQuery = "insert into developers(first_name, last_name, account_id) " +
                    "values ('"+model.getFirstName()+"', '"+model.getLastName()+"', '"+model.getAccount().getId()+"');";
            statement.execute(insertToDevelopersTableQuery);
            ResultSet resultSet = statement.executeQuery("select max(id) from developers;");
            resultSet.first();
            long developerId = resultSet.getLong(1);
            for (Skill skill : model.getSkills()) {
                statement.addBatch("insert into developer_skill(developer_id, skill_id) " +
                        "values ('" + developerId + "', '" + skill.getId() + "');");
            }
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            log.debug("Create entry(DB): "+model);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            log.error("Wrong SQL query to DB in creation", e);
            throw new RepoStorageException("Wrong SQL query to DB in creation");
        }
    }
    @Override
    public Developer getById(Long readID) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            String selectQuery = "select d.id, d.first_name, d.last_name, s.id, s.name, a.id, a.name, a.status " +
                    "from developers d " +
                    "left join (" +
                    "select ds.developer_id, s.id, s.name " +
                    "from developer_skill ds " +
                    "inner join skills s " +
                    "on ds.skill_id = s.id) s " +
                    "on d.id = s.developer_id " +
                    "left join accounts a " +
                    "on d.account_id = a.id " +
                    "where d.id='"+readID+"';";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            connection.commit();
            connection.setAutoCommit(true);
            Developer developer = new JDBCDeveloperMapper().map(resultSet, readID);
            log.debug("Read entry(DB) with ID: "+readID);
            return developer;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Developer updatedModel) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            String updateQuery = "update developers " +
                    "set first_name='"+updatedModel.getFirstName()+"', " +
                    "last_name='" +updatedModel.getLastName()+ "', " +
                    "account_id='"+updatedModel.getAccount().getId()+"' " +
                    "where id='"+updatedModel.getId()+"';";
            if(statement.executeUpdate(updateQuery)<1){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                log.warn("No such entry: "+updatedModel);
                throw new NoSuchEntryException("Updating in DB is failed");
            }
            statement.execute("delete from developer_skill where developer_id='"+updatedModel.getId()+"';");
            for (Skill skill : updatedModel.getSkills()) {
                statement.addBatch("insert into developer_skill(developer_id, skill_id) " +
                        "values ('" + updatedModel.getId() + "', '" + skill.getId() + "');");
            }
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            log.debug("Update entry(DB): "+updatedModel);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            log.error("Wrong SQL query to DB in updating", e);
            throw new RepoStorageException("Wrong SQL query to DB in updating");
        }
    }
    @Override
    public void delete(Long deletedEntry) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            statement.execute("delete from developer_skill where developer_id='"+deletedEntry+"';");
            String deleteQuery = "delete from developers where id='"+deletedEntry+"';";
            if(statement.executeUpdate(deleteQuery)<1){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                log.warn("No such entry with ID: "+deletedEntry);
                throw new NoSuchEntryException("Deleting in DB is failed");
            }
            connection.commit();
            connection.setAutoCommit(true);
            log.debug("Delete entry(DB) with ID: "+deletedEntry);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            log.error("Wrong SQL query to DB in deleting", e);
            throw new RepoStorageException("Wrong SQL query to DB in deleting");
        }
    }
    @Override
    public List<Developer> getAll() throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            String selectQuery = "select d.id, d.first_name, d.last_name, s.id, s.name, a.id, a.name, a.status " +
                    "from developers d " +
                    "left join (" +
                    "select ds.developer_id, s.id, s.name " +
                    "from developer_skill ds " +
                    "inner join skills s " +
                    "on ds.skill_id = s.id) s " +
                    "on d.id = s.developer_id " +
                    "left join accounts a " +
                    "on d.account_id = a.id;";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            ArrayList<Developer> developers = new ArrayList<>();
            JDBCDeveloperMapper mapper = new JDBCDeveloperMapper();
            long index = -1;
            while (resultSet.next()){
                if(index!=resultSet.getLong(1)){
                    index = resultSet.getLong(1);
                    developers.add(mapper.map(resultSet, index));
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
            log.debug("Read all entries(DB)");
            return developers;
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
