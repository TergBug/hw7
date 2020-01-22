package org.mycode.mapping;

import org.apache.log4j.Logger;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.model.Developer;
import org.mycode.model.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class JDBCDeveloperMapper implements Mapper<Developer, ResultSet, Long> {
    private static final Logger log = Logger.getLogger(JDBCDeveloperMapper.class);
    @Override
    public Developer map(ResultSet source, Long searchId) throws SQLException, NoSuchEntryException {
        int currentRow = source.getRow();
        source.beforeFirst();
        long id = -1;
        String firstName = "";
        String lastName = "";
        Set<Skill> skills = new HashSet<>();
        Account account = null;
        while (source.next()){
            if(source.getLong(1)==searchId){
                id = source.getLong(1);
                firstName = source.getString(2);
                lastName = source.getString(3);
                if(source.getLong(4)!=0L && source.getString(5)!=null){
                    skills.add(new Skill(source.getLong(4), source.getString(5)));
                }
                account = (source.getString(6)==null) ? null : new Account(source.getLong(6),
                        source.getString(7),
                        AccountStatus.valueOf(source.getString(8)));
            }
        }
        source.absolute(currentRow);
        if(id==-1){
            log.warn("No such entry with ID: "+searchId);
            throw new NoSuchEntryException("Reading from DB is failed");
        }
        return new Developer(id, firstName, lastName, skills, account);
    }
}
