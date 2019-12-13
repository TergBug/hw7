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
        if(model.getId()==null || model.getId()<1) {
            model.setId(generateAutoIncrId());
        }
        else if(getAll().stream().anyMatch(el-> el.getId().equals(model.getId()))){
            throw new NotUniquePrimaryKeyException("Creating of entry is failed");
        }
        String entry = patternOfEntry.replace("-1-", String.valueOf(model.getId())).
                replace("-2-", model.getName()).
                replace("-3-", model.getStatus().toString());
        try (FileWriter fw = new FileWriter(repo, true)){
            fw.append(entry);
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    private Long generateAutoIncrId() throws InvalidRepoFileException {
        Long id = 1L;
        List<Account> accounts = getAll();
        if(accounts.size()!=0){
            accounts.sort(Comparator.comparingLong(Account::getId));
            int index = 0;
            while (id.equals(accounts.get((index == accounts.size() - 1) ? index : index++).getId())){
                id++;
            }
        }
        return id;
    }
    @Override
    public Account getById(Long readID) throws InvalidRepoFileException, NoSuchEntryException, NotUniquePrimaryKeyException {
        List<Account> listOfReadAccounts = getAll().stream().filter(el-> el.getId().equals(readID)).collect(Collectors.toList());
        if(listOfReadAccounts.size()==0){
            throw new NoSuchEntryException("Reading of entry is failed");
        }
        else if(listOfReadAccounts.size()>1){
            throw new NotUniquePrimaryKeyException("Reading of entry is failed");
        }
        return listOfReadAccounts.get(0);
    }
    @Override
    public void update(Account updatedModel) throws InvalidRepoFileException, NoSuchEntryException {
        List<Account> listOfAccounts = getAll();
        boolean isExist = false;
        for (int i = 0; i < listOfAccounts.size(); i++) {
            if(listOfAccounts.get(i).getId().equals(updatedModel.getId())){
                isExist = true;
                listOfAccounts.set(i, updatedModel);
            }
        }
        if(!isExist){
            throw new NoSuchEntryException("Updating of entry is failed");
        }
        setAll(listOfAccounts);
    }
    @Override
    public void delete(Long deletedID) throws NoSuchEntryException, InvalidRepoFileException {
        List<Account> listOfAccounts = getAll();
        if(!listOfAccounts.removeIf(el -> el.getId().equals(deletedID))){
            throw new NoSuchEntryException("Deleting of entry is failed");
        }
        setAll(listOfAccounts);
    }
    @Override
    public List<Account> getAll() throws InvalidRepoFileException {
        String content = getContentFromFile(repo, validationPattern);
        if(content==null){
            throw new InvalidRepoFileException("Extracting of content from file is failed");
        }
        Matcher outerMatcher = Pattern.compile("<\\{\\*\\d+?\\*}\\{.*?}\\{((ACTIVE)|(BANNED)|(DELETED))}>").matcher(content);
        Matcher innerMatcher;
        List<Account> accounts = new ArrayList<>();
        while (outerMatcher.find()){
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            Long id = Long.parseLong(findInMatcherByIndex(innerMatcher, 1).group().replaceAll("[{*}]", ""));
            String name = findInMatcherByIndex(innerMatcher, 2).group().replaceAll("[{}]", "");
            AccountStatus status = AccountStatus.valueOf(findInMatcherByIndex(innerMatcher, 3).group().replaceAll("[{}]", ""));
            accounts.add(new Account(id, name, status));
        }
        return accounts;
    }
    private Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private void setAll(List<Account> listOfAccounts){
        StringBuilder content = new StringBuilder();
        for (Account listOfAccount : listOfAccounts) {
            content.append(patternOfEntry.replace("-1-", String.valueOf(listOfAccount.getId())).
                    replace("-2-", listOfAccount.getName()).
                    replace("-3-", listOfAccount.getStatus().toString()));
        }
        try (FileWriter fw = new FileWriter(repo, false)){
            fw.append(content.toString());
            fw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
