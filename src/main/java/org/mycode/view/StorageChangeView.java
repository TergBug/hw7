package org.mycode.view;

import org.mycode.controller.AccountController;
import org.mycode.controller.DeveloperController;
import org.mycode.controller.SkillController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;

public class StorageChangeView {
    private final String messageMenu = "0.Back\n1.Database\n2.Files";
    private final String messageChoose = "Choose item of menu: ";
    private final String messageWrongEntering = "Look rules!";
    private final String patternOfMenuChoice = "[012]";
    public boolean viewStorageChangeMenu(){
        System.out.println(messageMenu);
        try {
            switch (AppView.validationInt(patternOfMenuChoice, messageChoose, messageWrongEntering)){
                case 0:
                    return true;
                case 1:
                    SkillController.getInstance().request("db");
                    AccountController.getInstance().request("db");
                    DeveloperController.getInstance().request("db");
                    break;
                case 2:
                    SkillController.getInstance().request("f");
                    AccountController.getInstance().request("f");
                    DeveloperController.getInstance().request("f");
                    break;
            }
        } catch (IncorrectRequestException | RepoStorageException e) {
            e.printStackTrace();
        }
        return false;
    }
}
