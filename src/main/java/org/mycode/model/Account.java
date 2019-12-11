package org.mycode.model;

public class Account {
    private long ID;
    private String accountName;
    private AccountStatus status;
    public Account(long ID, String accountName, AccountStatus status) {
        this.ID = ID;
        this.accountName = accountName;
        this.status = status;
    }
    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
