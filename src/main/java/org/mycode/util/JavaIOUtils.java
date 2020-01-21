package org.mycode.util;

import org.mycode.exceptions.RepoStorageException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaIOUtils {
    private final static String LINK_TO_CONFIG = "./src/main/resources/config.properties";
    private static File skillRepo;
    private static File accountRepo;
    private static File developerRepo;
    static {
        Properties properties = new Properties();
        try(FileReader fr = new FileReader(LINK_TO_CONFIG)){
            properties.load(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        skillRepo = new File(properties.getProperty("javaio.file.path.skills"));
        accountRepo = new File(properties.getProperty("javaio.file.path.accounts"));
        developerRepo = new File(properties.getProperty("javaio.file.path.developers"));
    }
    public static File getSkillRepo(){
        return skillRepo;
    }
    public static File getAccountRepo() {
        return accountRepo;
    }
    public static File getDeveloperRepo() {
        return developerRepo;
    }
    public static List<String[]> getContentFromFile(File file, String validPattern) throws RepoStorageException {
        if(!file.exists()){
            throw new RepoStorageException("Extracting of content from file is failed");
        }
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file)){
            int c;
            while ((c=fr.read()) != -1) content.append((char) c);
        } catch (IOException e) { e.printStackTrace(); }
        List<String[]> contentTable = new ArrayList<>();
        Matcher outerMatcher = Pattern.compile(validPattern).matcher(content);
        Matcher innerMatcher;
        while (outerMatcher.find()){
            innerMatcher = Pattern.compile("\\{.*?}").matcher(outerMatcher.group());
            contentTable.add(new String[getMaxIndexOfMatcher(innerMatcher)]);
            for (int i = 0; i < contentTable.get(contentTable.size()-1).length; i++) {
                contentTable.get(contentTable.size()-1)[i] = findInMatcherByIndex(innerMatcher, i+1).group().replaceAll("]\\[", " ").replaceAll("[{*\\[\\]}]", "");
            }
        }
        if(contentTable.size()==0 && content.length()>0){
            throw new RepoStorageException("Extracting of content from file is failed");
        }
        return contentTable;
    }
    private static Matcher findInMatcherByIndex(Matcher matcher, int index){
        matcher.reset();
        for (int i = 0; i < index && matcher.find(); i++);
        return matcher;
    }
    private static int getMaxIndexOfMatcher(Matcher matcher){
        matcher.reset();
        int maxIndex;
        for (maxIndex = 0; matcher.find(); maxIndex++);
        matcher.reset();
        return maxIndex;
    }
    public static Long generateAutoIncrId(File file, String validPattern) throws RepoStorageException {
        List<String[]> content = getContentFromFile(file, validPattern);
        long id = 1L;
        if (content.size()!=0){
            content.sort(Comparator.comparing(el -> el[0]));
            id = Long.parseLong(content.get(content.size()-1)[0])+1;
        }
        return id;
    }
}
