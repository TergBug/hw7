package org.mycode.controller;

import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Skill;
import org.mycode.repository.SkillRepository;
import org.mycode.repository.javaio.JavaIOSkillRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class SkillController {
    private final String patternOfRequest = "(c\\|\\d+\\|[^|]*)|(r\\|\\d+)|(u\\|\\d+\\|[^|]*)|(d\\|\\d+)|(g)";
    private SkillRepository repo = new JavaIOSkillRepositoryImpl();
    public List<Skill> request(String requestStr) throws IncorrectRequestException {
        if(!requestStr.matches(patternOfRequest)) throw new IncorrectRequestException();
        String[] req = requestStr.split("\\|");
        List<Skill> skills = new ArrayList<>();
        try {
            switch (req[0]){
                case "c":
                    repo.create(new Skill(Long.parseLong(req[1]), req[2]));
                    break;
                case "r":
                    skills.add(repo.getById(Long.parseLong(req[1])));
                    break;
                case "u":
                    repo.update(new Skill(Long.parseLong(req[1]), req[2]));
                    break;
                case "d":
                    repo.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    skills = repo.getAll();
                    break;
            }
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException | NoSuchEntryException e) {
            System.out.println(e.toString());
        }
        return skills;
    }
}
