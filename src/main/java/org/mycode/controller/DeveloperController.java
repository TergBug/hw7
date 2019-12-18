package org.mycode.controller;

import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.repository.DeveloperRepository;
import org.mycode.repository.javaio.JavaIODeveloperRepositoryImpl;

import java.util.*;
import java.util.stream.Collectors;

public class DeveloperController {
    private final String patternOfRequest = "(c\\|\\d+\\|[^|]*\\|[^|]*\\|(\\d+,?)*\\|\\d+)|(r\\|\\d+)|(u\\|\\d+\\|[^|]*\\|[^|]*\\|(\\d+,?)*\\|\\d+)|(d\\|\\d+)|(g)";
    private DeveloperRepository repo = new JavaIODeveloperRepositoryImpl();
    public List<Developer> request(String requestStr) throws IncorrectRequestException {
        if(!requestStr.matches(patternOfRequest)) throw new IncorrectRequestException();
        String[] req = requestStr.split("\\|");
        List<Developer> developers = new ArrayList<>();
        Set<Skill> skillSet;
        try {
            switch (req[0]){
                case "c":
                    skillSet = Arrays.stream(req[4].split(",")).map(el -> new Skill(Long.parseLong(el))).collect(Collectors.toSet());
                    repo.create(new Developer(Long.parseLong(req[1]), req[2], req[3], skillSet, new Account(Long.parseLong(req[5]))));
                    break;
                case "r":
                    developers.add(repo.getById(Long.parseLong(req[1])));
                    break;
                case "u":
                    skillSet = Arrays.stream(req[4].split(",")).map(el -> new Skill(Long.parseLong(el))).collect(Collectors.toSet());
                    repo.update(new Developer(Long.parseLong(req[1]), req[2], req[3], skillSet, new Account(Long.parseLong(req[5]))));
                    break;
                case "d":
                    repo.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    developers = repo.getAll();
                    break;
            }
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException | NoSuchEntryException e) {
            System.out.println(e.toString());
        }
        return developers;
    }
}
