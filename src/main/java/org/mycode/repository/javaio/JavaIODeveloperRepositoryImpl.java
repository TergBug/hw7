package org.mycode.repository.javaio;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.repository.DeveloperRepository;

import java.util.List;

public class JavaIODeveloperRepositoryImpl implements DeveloperRepository {
    @Override
    public void create(Developer model) {

    }
    @Override
    public Developer read(Long readID) {
        return null;
    }
    @Override
    public void update(Developer model) {

    }
    @Override
    public void delete(Long deletedID) {

    }

    @Override
    public List<Developer> getAll() throws InvalidRepoFileException {
        return null;
    }
}
