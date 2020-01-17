package org.mycode.mapping;

import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.model.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCSkillMapper implements Mapper<Skill, ResultSet, Long> {
    @Override
    public Skill map(ResultSet source, Long searchId) throws SQLException, NoSuchEntryException {
        int currentRow = source.getRow();
        source.beforeFirst();
        long id = -1;
        String name = "";
        while (source.next()){
            if(source.getLong(1)==searchId){
                id = source.getLong(1);
                name = source.getString(2);
            }
        }
        source.absolute(currentRow);
        if(id==-1){
            throw new NoSuchEntryException("Reading from DB is failed");
        }
        return new Skill(id, name);
    }
}
