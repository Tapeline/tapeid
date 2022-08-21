package me.tapeline.tapeid.TapeID;

public class UserEntry {

    private String name;
    private String passHash;

    public UserEntry(String name, String passHash) {
        this.name = name;
        this.passHash = passHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
}
