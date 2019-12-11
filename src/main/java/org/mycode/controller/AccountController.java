package org.mycode.controller;

import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.repository.javaio.JavaIOAccountRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class AccountController {
    private final String patternOfRequest = "(c\\|\\d+\\|[^|]*\\|((a)|(b)|(d)))|(r\\|\\d+)|(u\\|\\d+\\|[^|]*\\|((a)|(b)|(d)))|(d\\|\\d+)|(g)";
    private JavaIOAccountRepositoryImpl repo = new JavaIOAccountRepositoryImpl();
    public List<Account> request(String requestStr) throws IncorrectRequestException {
        if(!requestStr.matches(patternOfRequest)) throw new IncorrectRequestException();
        String[] req = requestStr.split("\\|");
        List<Account> accounts = new ArrayList<>();
        try {
            switch (req[0]){
                case "c":
                    repo.create(new Account(Long.parseLong(req[1]), req[2], acronymToEnumStatus(req[3])));
                    break;
                case "r":
                    accounts.add(repo.read(Long.parseLong(req[1])));
                    break;
                case "u":
                    repo.update(new Account(Long.parseLong(req[1]), req[2], acronymToEnumStatus(req[3])));
                    break;
                case "d":
                    repo.delete(Long.parseLong(req[1]));
                    break;
                case "g":
                    accounts = repo.getAll();
                    break;
            }
        } catch (InvalidRepoFileException | NotUniquePrimaryKeyException | NoSuchEntryException e) {
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
