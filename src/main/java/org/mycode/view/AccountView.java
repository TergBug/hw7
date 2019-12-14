package org.mycode.view;

import org.mycode.controller.AccountController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.model.Account;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class AccountView {
    private final String messageMenu = "0.Go back\n1.Create\n2.Read\n3.Update\n4.Delete\n5.Get all";
    private final String messageEnterNameOfAccount = "Enter name of account: ";
    private final String messageEnterID = "Enter ID: ";
    private final String messageEnterStatus = "1.Active\n2.Banned\n3.Deleted\nChoose status: ";
    private final String messageChoose = "Choose item of menu: ";
    private final String messageWrongEntering = "Look rules!";
    private final String patternOfMenuChoice = "[012345]";
    private final String patternOfStatusChoice = "[123]";
    private final String patternOfID = "\\d+";
    public boolean viewAccountMenu(){
        System.out.println(messageMenu);
        String requestStr = "";
        switch (AppView.validationInt(patternOfMenuChoice, messageChoose, messageWrongEntering)){
            case 0:
                return true;
            case 1:
                requestStr="c|0|";
                System.out.print(messageEnterNameOfAccount);
                requestStr+=new Scanner(System.in).next()+"|";
                requestStr+=getStatusAcronymFromNum(AppView.validationInt(patternOfStatusChoice, messageEnterStatus, messageWrongEntering));
                break;
            case 2:
                requestStr="r|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 3:
                requestStr="u|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering)+"|";
                System.out.print(messageEnterNameOfAccount);
                requestStr+=new Scanner(System.in).next()+"|";
                requestStr+=getStatusAcronymFromNum(AppView.validationInt(patternOfStatusChoice, messageEnterStatus, messageWrongEntering));
                break;
            case 4:
                requestStr+="d|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 5:
                requestStr+="g";
                break;
        }
        try {
            List<Account> accountToView = new AccountController().request(requestStr);
            accountToView.sort(Comparator.comparingLong(Account::getId));
            accountToView.forEach(el -> System.out.println(el.toString()));
        } catch (IncorrectRequestException e) {
            System.out.println(e.toString());
        }
        System.out.println("------------");
        return false;
    }
    private char getStatusAcronymFromNum(int num){
        switch (num){
            case 1: return 'a';
            case 2: return 'b';
            case 3: return 'd';
            default: return '.';
        }
    }
}
