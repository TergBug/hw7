package org.mycode.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JavaIOUtils {
    private final static String LINK_TO_CONFIG = "./src/main/resources/config.properties";
    private final static String LINK_TO_SKILL_FILE;
    private final static String LINK_TO_ACCOUNT_FILE;
    private final static String LINK_TO_DEVELOPER_FILE;
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
        LINK_TO_SKILL_FILE = properties.getProperty("javaio.file.path.skills");
        LINK_TO_ACCOUNT_FILE = properties.getProperty("javaio.file.path.accounts");
        LINK_TO_DEVELOPER_FILE = properties.getProperty("javaio.file.path.developers");
        skillRepo = new File(LINK_TO_SKILL_FILE);
        accountRepo = new File(LINK_TO_ACCOUNT_FILE);
        developerRepo = new File(LINK_TO_DEVELOPER_FILE);
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
}
