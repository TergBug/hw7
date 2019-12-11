package org.mycode.model;

import java.util.Set;

public class Developer {
    private long ID;
    private String firstName;
    private String lastName;
    private Set<Skill> skills;
    private Account account;
    public Developer(long ID, String firstName, String lastName, Set<Skill> skills, Account account) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.account = account;
    }
    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Set<Skill> getSkills() {
        return skills;
    }
    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
}
