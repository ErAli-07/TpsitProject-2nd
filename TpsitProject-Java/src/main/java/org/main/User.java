package org.main;

import java.io.*;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Authentication of the password
    public boolean authenticate(String inputPassword) {
        return password.equals(inputPassword);
    }
}