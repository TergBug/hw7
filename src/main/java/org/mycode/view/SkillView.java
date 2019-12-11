package org.mycode.view;

import org.mycode.controller.SkillController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.model.Skill;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class SkillView {
    private final String messageMenu = "0.Go back\n1.Create\n2.Read\n3.Update\n4.Delete\n5.Get all";
    private final String messageEnterNameOfSkill = "Enter name of skill: ";
    private final String messageEnterID = "Enter ID: ";
    private final String messageChoose = "Choose item of menu: ";
    private final String messageWrongEntering = "Look rules!";
    private final String patternOfMenuChoice = "[012345]";
    private final String patternOfID = "\\d+";
    public boolean viewSkillMenu(){
        System.out.println(messageMenu);
        String requestStr = "";
        switch (AppView.validationInt(patternOfMenuChoice, messageChoose, messageWrongEntering)){
            case 0:
                return true;
            case 1:
                requestStr="c|0|";
                System.out.print(messageEnterNameOfSkill);
                requestStr+=new Scanner(System.in).next();
                break;
            case 2:
                requestStr="r|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 3:
                requestStr="u|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering)+"|";
                System.out.print(messageEnterNameOfSkill);
                requestStr+=new Scanner(System.in).next();
                break;
            case 4:
                requestStr+="d|"+AppView.validationInt(patternOfID, messageEnterID, messageWrongEntering);
                break;
            case 5:
                requestStr+="g";
                break;
        }
        try {
            List<Skill> skillsToView = new SkillController().request(requestStr);
            skillsToView.sort(Comparator.comparingLong(Skill::getID));
            skillsToView.forEach(el -> System.out.println(el.getID()+" "+el.getSkillName()));
        } catch (IncorrectRequestException e) {
            System.out.println(e.toString());
        }
        System.out.println("------------");
        return false;
    }
}
