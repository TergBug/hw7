package org.mycode.repository.jdbc;

import org.mycode.exceptions.RepoStorageException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.mapping.JDBCAccountMapper;
import org.mycode.model.Account;
import org.mycode.repository.AccountRepository;
import org.mycode.util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCAccountRepositoryImpl implements AccountRepository {
    private Connection connection;
    public JDBCAccountRepositoryImpl() throws RepoStorageException {
        JDBCUtils.makeConnectionToDB();
        connection = JDBCUtils.getConnection();
    }
    @Override
    public void create(Account model) throws RepoStorageException {
        try (Statement statement = connection.createStatement()){
            String insertQuery = "insert into accounts(name, status) " +
                    "values ('"+model.getName()+"', '"+model.getStatus().toString()+"');";
            statement.execute(insertQuery);
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in creation");
        }
    }
    @Override
    public Account getById(Long readID) throws RepoStorageException, NoSuchEntryException, NotUniquePrimaryKeyException {
        try (Statement statement = connection.createStatement()){
            String selectQuery = "select * from accounts where id='"+readID+"';";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            if(resultSet.absolute(2)){
                throw new NotUniquePrimaryKeyException("Reading from DB is failed");
            }
            resultSet.first();
            return new JDBCAccountMapper().map(resultSet, readID);
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
    @Override
    public void update(Account updatedModel) throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String updateQuery = "update accounts " +
                    "set name='"+updatedModel.getName()+"', status='" +updatedModel.getStatus().toString()+ "' " +
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
            String deleteQuery = "delete from accounts where id='"+deletedEntry+"';";
            if(statement.executeUpdate(deleteQuery)<1){
                throw new NoSuchEntryException("Deleting in DB is failed");
            }
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in deleting");
        }
    }
    @Override
    public List<Account> getAll() throws RepoStorageException, NoSuchEntryException {
        try (Statement statement = connection.createStatement()){
            String selectQuery = "select * from accounts;";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            ArrayList<Account> accounts = new ArrayList<>();
            JDBCAccountMapper mapper = new JDBCAccountMapper();
            while (resultSet.next()){
                accounts.add(mapper.map(resultSet, resultSet.getLong(1)));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RepoStorageException("Wrong SQL query to DB in reading");
        }
    }
}
