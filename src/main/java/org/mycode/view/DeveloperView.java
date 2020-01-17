package org.mycode.view;

import org.mycode.controller.AccountController;
import org.mycode.controller.DeveloperController;
import org.mycode.controller.SkillController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;
import org.mycode.model.Developer;
import org.mycode.model.Skill;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class DeveloperView {
    private final String messageMenu = "0.Go back\n1.Create\n2.Read\n3.Update\n4.Delete\n5.Get all";
    private final String messageEnterFirstName = "Enter first name of developer: ";
    private final String messageEnterLastName = "Enter last name of developer: ";
    private final String messageChooseSkill = "#Choose skill: ";
    private final String messageChooseAccount = "#Choose account: ";
    private final String messageEnterID = "Enter ID: ";
    private final String messageChoose = "Choose item of menu: ";
    private final String messageWrongEntering = "Look rules!";
    private final String patternOfMenuChoice = "[012345]";
    private final String patternOfID = "\\d+";
    public boolean viewDeveloperMenu(){
        Scanner scanner = new Scanner(System.in);
        StringBuilder skills = new StringBuilder("0 No more skill\n");
        StringBuilder accounts = new StringBuilder();
        try {
            List<Skill> skillList = SkillController.getInstance().request("g");
            skillList.sort(Comparator.comparingLong(Skill::getId));
            skillList.forEach(el -> skills.append(el.toString()+"\n"));
            List<Account> accountList = AccountController.getInstance().request("g");
            accountList.sort(Comparator.comparingLong(Account::getId));
            accountList.forEach(el -> accounts.append(el.toString()+"\n"));
        } catch (IncorrectRequestException | RepoStorageException e) {
            e.printStackTrace();
        }
        String messageChooseSkill = this.messageChooseSkill.replace("#", skills.toString());
        String messageChooseAccount = this.messageChooseAccount.replace("#", accounts.toString());
        System.out.println(messageMenu);
        String requestStr = "";
        switch (AppView.validationInt(patternOfMenuChoice, messageChoose, messageWrongEntering)){
            case 0:
                return true;
            case 1:
                requestStr="c|0|";
                System.out.print(messageEnterFirstName);
                requestStr+=scanner.next()+"|";
                System.out.print(messageEnterLastName);
                requestStr+=scanner.next()+"|";
                requestStr+=enteringSkills(messageChooseSkill)+"|"+AppView.validationInt(patternOfID, messageChooseAccount, messageWrongEntering);
                break;
            case 2:
                requestStr="r|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 3:
                requestStr="u|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering)+"|";
                System.out.print(messageEnterFirstName);
                requestStr+=scanner.next()+"|";
                System.out.print(messageEnterLastName);
                requestStr+=scanner.next()+"|";
                requestStr+=enteringSkills(messageChooseSkill)+"|"+AppView.validationInt(patternOfID, messageChooseAccount, messageWrongEntering);
                break;
            case 4:
                requestStr="d|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 5:
                requestStr="g";
                break;
        }
        try {
            List<Developer> developerToView = DeveloperController.getInstance().request(requestStr);
            developerToView.sort(Comparator.comparingLong(Developer::getId));
            developerToView.forEach(el -> System.out.println(el.toString()));
        } catch (IncorrectRequestException | RepoStorageException e) {
            System.out.println(e.toString());
        }
        System.out.println("------------");
        return false;
    }
    private String enteringSkills(String menu){
        Scanner scanner = new Scanner(System.in);
        String skillFK = "";
        while (true){
            int enteredId = AppView.validationInt(patternOfID, menu, messageWrongEntering);
            if(enteredId==0) return skillFK.substring(0, skillFK.length()-1);
            skillFK += enteredId+",";
        }
    }
}
