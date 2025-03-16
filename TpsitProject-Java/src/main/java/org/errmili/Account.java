package org.errmili;

import java.io.*;
import java.util.*;

public class Account {
    private double balance;
    private boolean isInDebt;
    private static final String ACCOUNT_DIRECTORY = "Accounts/"; // Name of the directory

    public Account(double initialBalance) {
        this.balance = initialBalance;
        this.isInDebt = (balance < 0);
    }

    // Getter of Balance
    public double getBalance() {
        return balance;
    }

    // Returns if is in Debt or not
    public boolean isInDebt() {
        return isInDebt;
    }

    // Deposits and Withdraw money
    public boolean deposit(double amount) {
        if (amount <= 0) return false;

        balance += amount;
        updateDebtStatus();
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;

        balance -= amount;
        updateDebtStatus();
        return true;
    }

    // Updates the Debt Status
    private void updateDebtStatus() {
        isInDebt = (balance < 0);
    }

    public void printInfo() {
        System.out.println("Account balance: " + balance + " EUR");
        if (isInDebt) {
            System.out.println("Account is in debt. New investments are blocked until debt is repaid.");
        }
    }

    // Saves the information tied with the account
    public void saveToFile(String username) {
        File directory = new File(ACCOUNT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = ACCOUNT_DIRECTORY + username + "_account.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(balance);
        } catch (IOException e) {
            System.err.println("Error saving account data for " + username + ": " + e.getMessage());
        }
    }

    // Loads the information tied with the account
    public static Account loadFromFile(String username) {
        String filename = ACCOUNT_DIRECTORY + username + "_account.txt";
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("No existing account data found for " + username + ". Creating new account.");
            return new Account(0);
        }

        try (Scanner scanner = new Scanner(file)) {
            double balance = Double.parseDouble(scanner.nextLine());
            System.out.println("Account data for " + username + " loaded successfully.");
            return new Account(balance);
        } catch (IOException e) {
            System.out.println("Error loading account: " + e.getMessage() + ". Creating new account.");
            return new Account(0);
        }
    }
}