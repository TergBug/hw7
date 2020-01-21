package org.mycode.controller;

import org.apache.log4j.Logger;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;
import org.mycode.service.SkillService;
import org.mycode.service.TypeOfStorage;

import java.util.ArrayList;
import java.util.List;

public class SkillController {
    private static final Logger log = Logger.getLogger(SkillController.class);
    private final String PATTERN_OF_REQUEST = "(f)|(db)|(c\\|\\d+\\|[^|]*)|(r\\|\\d+)|(u\\|\\d+\\|[^|]*)|(d\\|\\d+)|(g)";
    private SkillService service;
    private static SkillController instance;
    private SkillController() throws RepoStorageException {
        service = new SkillService();
    }
    public static synchronized SkillController getInstance() throws RepoStorageException {
        if(instance==null){
            instance = new SkillController();
        }
        return instance;
    }
    public List<Skill> request(String requestStr) throws IncorrectRequestException {
        log.debug("Controller handle request: "+requestStr);
        if(!requestStr.matches(PATTERN_OF_REQUEST)) {
            log.warn("Incorrect request: "+requestStr);
            throw new IncorrectRequestException();
        }
        String[] req = requestStr.split("\\|");
        List<Skill> skills = new ArrayList<>();
        try {
            switch (req[0]){
                case "f":
                    service.changeStorage(TypeOfStorage.FILES);
                    break;
                case "db":
                    service.changeStorage(TypeOfStorage.DATABASE);
                    break;
                case "c":
                    service.create(new Skill(Long.parseLong(req[1]), req[2]));
                    break;
                case "r":
                    skills.add(service.getById(Long.parseLong(req[1])));
                    break;
                case "u":
                    service.update(new Skill(Long.parseLong(req[1]), req[2]));
                    break;
                case "d":
                    service.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    skills = service.getAll();
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return skills;
    }
}
