package org.mycode.util;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.mycode.exceptions.RepoStorageException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    private final static String LINK_TO_CONFIG = "./src/main/resources/config.properties";
    private final static String JDBC_DRIVER;
    private final static String DB_URL;
    private final static String DB_USER;
    private final static String DB_PASSWORD;
    private final static String LINK_TO_SQL_SCRIPT;
    private static Connection connection;
    static {
        Properties properties = new Properties();
        try(FileReader fr = new FileReader(LINK_TO_CONFIG)){
            properties.load(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JDBC_DRIVER = properties.getProperty("jdbc.driver");
        DB_URL = properties.getProperty("jdbc.url");
        DB_USER = properties.getProperty("jdbc.user");
        DB_PASSWORD = properties.getProperty("jdbc.password");
        LINK_TO_SQL_SCRIPT = properties.getProperty("jdbc.sql.init.link");
    }
    public static void makeConnectionToDB() throws RepoStorageException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            if(e instanceof SQLException){
                throw new RepoStorageException("Cannot connect to SQL DB");
            } else if(e instanceof ClassNotFoundException){
                throw new RepoStorageException("Invalid JDBC driver");
            }
        }
        validateDatabase();
    }
    private static void validateDatabase(){
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
    public static Connection getConnection(){
        return connection;
    }
}
