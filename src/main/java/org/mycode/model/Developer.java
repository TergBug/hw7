package org.mycode.model;

import java.util.Objects;
import java.util.Set;

public class Developer {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<Skill> skills;
    private Account account;
    public Developer(Long id, String firstName, String lastName, Set<Skill> skills, Account account) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.account = account;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(id+" "+firstName+" "+lastName+"\nSkills: ");
        skills.forEach(el -> str.append(el.getName()+" "));
        str.append("\nAccount: "+((account==null || account.getName()==null || account.getStatus()==null) ? "null" : (account.getName()+" "+account.getStatus().toString())));
        return str.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Developer developer = (Developer) o;
        return Objects.equals(id, developer.id) &&
                Objects.equals(firstName, developer.firstName) &&
                Objects.equals(lastName, developer.lastName) &&
                Objects.equals(skills, developer.skills) &&
                Objects.equals(account, developer.account);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, skills, account);
    }
}
