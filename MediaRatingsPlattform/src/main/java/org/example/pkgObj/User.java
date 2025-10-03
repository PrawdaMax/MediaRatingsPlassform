package org.example.pkgObj;

import com.google.gson.Gson;

public class User {

    private String Username;
    private String Password;

    public User (String username, String password) {
        this.Password = password;
        this.Username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
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
