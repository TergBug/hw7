package org.mycode.repository.jdbc;

import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.mapping.JDBCAccountMapper;
import org.mycode.model.Account;
import org.mycode.repository.AccountRepository;
import org.mycode.util.JDBCConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCAccountRepositoryImpl implements AccountRepository {
    private static final Logger log = Logger.getLogger(JDBCAccountRepositoryImpl.class);
    private final String INSERT_QUERY = "insert into accounts(name, status) values (?, ?);";
    private final String SELECT_QUERY = "select * from accounts where id=?;";
    private final String UPDATE_QUERY = "update accounts set name=?, status=? where id=?;";
    private final String DELETE_QUERY = "delete from accounts where id=?;";
    private final String SELECT_ALL_QUERY = "select * from accounts;";
    private Connection connection;
    public JDBCAccountRepositoryImpl() throws RepoStorageException {
        try {
            connection = JDBCConnectionUtil.getConnection();
        } catch (SQLException e) {
            log.error("Cannot connect to SQL DB", e);
            throw new RepoStorageException("Cannot connect to SQL DB");
        }
    }
    @Override
    public void create(Account model) throws RepoStorageException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)){
            statement.setString(1, model.getName());
            statement.setString(2, model.getStatus().toString());
            statement.execute();
            log.debug("Create entry(DB): "+model);
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in creation", e);
            throw new RepoStorageException("Wrong SQL query to DB in creation");
        }
    }
    @Override
    public Account getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)){
            statement.setLong(1, readID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.absolute(2)){
                throw new NotUniquePrimaryKeyException("Reading from DB is failed");
            }
            resultSet.first();
            log.debug("Read entry(DB) with ID: "+readID);
            return new JDBCAccountMapper().map(resultSet, readID);
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Account updatedModel) throws RepoStorageException, NoSuchEntryException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)){
            statement.setString(1, updatedModel.getName());
            statement.setString(2, updatedModel.getStatus().toString());
            statement.setLong(3, updatedModel.getId());
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
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)){
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
    public List<Account> getAll() throws RepoStorageException, NoSuchEntryException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)){
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Account> accounts = new ArrayList<>();
            JDBCAccountMapper mapper = new JDBCAccountMapper();
            while (resultSet.next()){
                accounts.add(mapper.map(resultSet, resultSet.getLong(1)));
            }
            log.debug("Read all entries(DB)");
            return accounts;
        } catch (SQLException e) {
            log.error("Wrong SQL query to DB in reading", e);
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
