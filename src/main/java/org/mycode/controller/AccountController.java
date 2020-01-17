package org.mycode.controller;

import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.service.AccountService;
import org.mycode.service.TypeOfStorage;

import java.util.ArrayList;
import java.util.List;

public class AccountController {
    private final String PATTERN_OF_REQUEST = "(f)|(db)|(c\\|\\d+\\|[^|]*\\|((a)|(b)|(d)))|(r\\|\\d+)|(u\\|\\d+\\|[^|]*\\|((a)|(b)|(d)))|(d\\|\\d+)|(g)";
    private AccountService service;
    private static AccountController instance;
    private AccountController() throws RepoStorageException {
        service = new AccountService();
    }
    public static synchronized AccountController getInstance() throws RepoStorageException {
        if(instance==null){
            instance = new AccountController();
        }
        return instance;
    }
    public List<Account> request(String requestStr) throws IncorrectRequestException {
        if(!requestStr.matches(PATTERN_OF_REQUEST)) throw new IncorrectRequestException();
        String[] req = requestStr.split("\\|");
        List<Account> accounts = new ArrayList<>();
        try {
            switch (req[0]){
                case "f":
                    service.changeStorage(TypeOfStorage.FILES);
                    break;
                case "db":
                    service.changeStorage(TypeOfStorage.DATABASE);
                    break;
                case "c":
                    service.create(new Account(Long.parseLong(req[1]), req[2], acronymToEnumStatus(req[3])));
                    break;
                case "r":
                    accounts.add(service.getById(Long.parseLong(req[1])));
                    break;
                case "u":
                    service.update(new Account(Long.parseLong(req[1]), req[2], acronymToEnumStatus(req[3])));
                    break;
                case "d":
                    service.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    accounts = service.getAll();
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts;
    }
    private AccountStatus acronymToEnumStatus(String acronym){
        switch (acronym){
            case "a": return AccountStatus.ACTIVE;
            case "b": return AccountStatus.BANNED;
            case "d": return AccountStatus.DELETED;
            default: return null;
        }
    }
}
