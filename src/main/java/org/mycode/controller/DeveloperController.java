package org.mycode.controller;

import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.Developer;
import org.mycode.model.Skill;
import org.mycode.service.DeveloperService;
import org.mycode.service.TypeOfStorage;

import java.util.*;
import java.util.stream.Collectors;

public class DeveloperController {
    private final String PATTERN_OF_REQUEST = "(f)|(db)|(c\\|\\d+\\|[^|]*\\|[^|]*\\|(\\d+,?)*\\|\\d+)|(r\\|\\d+)|(u\\|\\d+\\|[^|]*\\|[^|]*\\|(\\d+,?)*\\|\\d+)|(d\\|\\d+)|(g)";
    private DeveloperService service;
    private static DeveloperController instance;
    private DeveloperController() throws RepoStorageException {
        service = new DeveloperService();
    }
    public static synchronized DeveloperController getInstance() throws RepoStorageException {
        if(instance==null){
            instance = new DeveloperController();
        }
        return instance;
    }
    public List<Developer> request(String requestStr) throws IncorrectRequestException {
        if(!requestStr.matches(PATTERN_OF_REQUEST)) throw new IncorrectRequestException();
        String[] req = requestStr.split("\\|");
        List<Developer> developers = new ArrayList<>();
        Set<Skill> skillSet;
        try {
            switch (req[0]){
                case "f":
                    service.changeStorage(TypeOfStorage.FILES);
                    break;
                case "db":
                    service.changeStorage(TypeOfStorage.DATABASE);
                    break;
                case "c":
                    skillSet = Arrays.stream(req[4].split(",")).map(el -> new Skill(Long.parseLong(el))).collect(Collectors.toSet());
                    service.create(new Developer(Long.parseLong(req[1]), req[2], req[3], skillSet, new Account(Long.parseLong(req[5]))));
                    break;
                case "r":
                    developers.add(service.getById(Long.parseLong(req[1])));
                    break;
                case "u":
                    skillSet = Arrays.stream(req[4].split(",")).map(el -> new Skill(Long.parseLong(el))).collect(Collectors.toSet());
                    service.update(new Developer(Long.parseLong(req[1]), req[2], req[3], skillSet, new Account(Long.parseLong(req[5]))));
                    break;
                case "d":
                    service.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    developers = service.getAll();
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return developers;
    }
}
