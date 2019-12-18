package org.mycode.model;

import java.util.Objects;

public class Account {
    private Long id;
    private String name;
    private AccountStatus status;
    public Account(Long id) {
        this.id = id;
    }
    public Account(Long id, String name, AccountStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return id+" "+name+" "+status.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(name, account.name) &&
                status == account.status;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }
}
