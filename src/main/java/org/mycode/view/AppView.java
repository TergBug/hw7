package org.mycode.view;

import java.util.Scanner;

public class AppView {
    private final String messageMenu = "0.Exit\n1.Developers\n2.Skills\n3.Accounts";
    private final String messageChoose = "Choose item of menu: ";
    private final String messageWrongEntering = "Look rules!";
    private final String patternOfMenuChoice = "[0123]";
    public void viewApp(){
        do{
            System.out.println(messageMenu);
            switch (validationInt(patternOfMenuChoice, messageChoose, messageWrongEntering)){
                case 0:
                    System.exit(0);
                case 1:
                    break;
                case 2:
                    SkillView skillView = new SkillView();
                    while (!skillView.viewSkillMenu());
                    break;
                case 3:
                    AccountView accountView = new AccountView();
                    while (!accountView.viewSkillMenu());
                    break;
            }
        }while (true);
    }
    static int validationInt(String pattern, String shownText, String shownTextIfWrong){
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print(shownText);
            if(!scanner.hasNext(pattern)){
                scanner.next();
                System.out.println(shownTextIfWrong);
                continue;
            }
            return scanner.nextInt();
        }while (true);
    }
}
