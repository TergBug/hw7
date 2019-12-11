package org.mycode.repository.javaio;

import org.mycode.exceptions.InvalidRepoFileException;
import org.mycode.exceptions.NoSuchEntryException;
import org.mycode.exceptions.NotUniquePrimaryKeyException;
import org.mycode.model.Account;
import org.mycode.model.AccountStatus;
import org.mycode.repository.AccountRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaIOAccountRepositoryImpl implements AccountRepository {
    private final String patternOfEntry = "<{*-1-*}{-2-}{-3-}>";
    private final String validationPattern = "(<\\{\\*\\d+\\*}\\{.*?}\\{((ACTIVE)|(BANNED)|(DELETED))}>.*)*";
    private final String linkToFile = "./src/main/resources/accounts.txt";
    private File repo;
    public JavaIOAccountRepositoryImpl(){
        repo = new File(linkToFile);
    }
    private String getContentFromFile(File file, String validPattern){
        if(!file.exists()) return null;
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file)){
            int c;
            while ((c=fr.read()) != -1) content.append((char) c);
        } catch (IOException e) { e.printStackTrace(); }
        String outContent = content.toString().replaceAll("(\\r\\n)|(\\r)|(\\n)", "");
        return outContent.matches(validPattern) ? outContent : null;
    }
    @Override
    public void create(Account model) throws InvalidRepoFileException, NotUniquePrimaryKeyException {
        if(!repo.exists()) {
            try {
                repo.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }
        if(model.getID()<1) {
            model.setID(1);
            List<Account> accounts = getAll();
            accounts.sort(Comparator.comparingLong(Account::getID));
            int index = 0;
            while (model.getID()==accounts.get((index==accounts.size()-1) ? index : index++).getID()) model.setID(model.getID()+1);
        }
        else if(getAll().stream().anyMatch(el->el.getID()==model.getID())) throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getID())).replace("-2-", model.getAccountName()).
                replace("-3-", model.getStatus().toString());
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public Account read(Long readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Account> listOfAccounts = getAll();
        listOfAccounts = listOfAccounts.stream().filter(el->el.getID()==readID).collect(Collectors.toList());
        if(listOfAccounts.size()==0) throw new NoSuchEntryException("Reading of entry is failed");
        if(listOfAccounts.size()>1) throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        return listOfAccounts.get(0);
    }
    @Override
    public void update(Account updatedModel) throws InvalidRepoFileException, NoSuchEntryException {
        List<Account> listOfAccounts = getAll();
        boolean isExist = false;
        for (int i = 0; i < listOfAccounts.size(); i++) {
            if(listOfAccounts.get(i).getID()==updatedModel.getID()){
                isExist = true;
                listOfAccounts.set(i, updatedModel);
            }
        }
        if(!isExist) throw new NoSuchEntryException("Updating of entry is failed");
        setAll(listOfAccounts);
    }
    @Override
    public void delete(Long deletedID) throws NoSuchEntryException, InvalidRepoFileException {
        List<Account> listOfAccounts = getAll();
        if(!listOfAccounts.removeIf(el->el.getID()==deletedID)) throw new NoSuchEntryException("Deleting of entry is failed");
        setAll(listOfAccounts);
    }
    @Override
    public List<Account> getAll() throws InvalidRepoFileException {
        String content = getContentFromFile(repo, validationPattern);
        if(content==null) throw new InvalidRepoFileException("Extracting of content from file is failed");
        Matcher outerMatcher = Pattern.compile("<\\{\\*\\d+?\\*}\\{.*?}\\{((ACTIVE)|(BANNED)|(DELETED))}>").matcher(content);
        Matcher innerMatcher;
        ArrayList<String[]> entriesList = new ArrayList<>();
        while (outerMatcher.find()){
            entriesList.add(new String[3]);
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            entriesList.get(entriesList.size()-1)[0] = findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", "");
            entriesList.get(entriesList.size()-1)[1] = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
            entriesList.get(entriesList.size()-1)[2] = findInMatcherByIndex(innerMatcher, 3).group().replaceAll("[{}]", "");
        }
        return entriesList.stream().map(el->new Account(Long.parseLong(el[0]), el[1], AccountStatus.valueOf(el[2]))).collect(Collectors.toList());
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<Account> listOfAccounts){
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < listOfAccounts.size(); i++)
            content.append(patternOfEntry.replace("-1-", String.valueOf(listOfAccounts.get(i).getID())).
                    replace("-2-", listOfAccounts.get(i).getAccountName()).
                    replace("-3-", listOfAccounts.get(i).getStatus().toString()));
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
