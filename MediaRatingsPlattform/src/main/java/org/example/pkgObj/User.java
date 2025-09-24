package org.example.pkgObj;

public class User {

    private String Username;
    private String Password;

    public User (String username, String password) {
        this.Password = password;
        this.Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
