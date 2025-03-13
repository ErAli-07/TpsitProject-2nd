import java.io.*;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = simpleHash(password);
    }

    public String getUsername() {
        return username;
    }

    // Authentication of the password
    public boolean authenticate(String password) {
        return simpleHash(password).equals(password);
    }

    // Generates a simple hash code for a given input string
    private String simpleHash(String input) {
        int hash = 7;
        for (int i = 0; i < input.length(); i++) {
            hash = hash * 31 + input.charAt(i);
        }
        return Integer.toHexString(hash);
    }
}