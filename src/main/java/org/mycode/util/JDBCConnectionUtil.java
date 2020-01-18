package org.mycode.util;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCConnectionUtil {
    private final static String LINK_TO_CONFIG = "./src/main/resources/config.properties";
    private final static String LINK_TO_SQL_SCRIPT;
    private static BasicDataSource ds = new BasicDataSource();
    static {
        Properties properties = new Properties();
        try(FileReader fr = new FileReader(LINK_TO_CONFIG)){
            properties.load(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ds.setDriverClassName(properties.getProperty("jdbc.driver"));
        ds.setUrl(properties.getProperty("jdbc.url"));
        ds.setUsername(properties.getProperty("jdbc.user"));
        ds.setPassword(properties.getProperty("jdbc.password"));
        LINK_TO_SQL_SCRIPT = properties.getProperty("jdbc.sql.init.link");
        validateDatabase();
    }
    private static void validateDatabase(){
        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()){
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
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
