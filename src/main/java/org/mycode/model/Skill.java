package org.mycode.model;

public class Skill {
    private long ID;
    private String skillName;
    public Skill(long ID, String skillName) {
        this.ID = ID;
        this.skillName = skillName;
    }
    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
    public String getSkillName() {
        return skillName;
    }
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
