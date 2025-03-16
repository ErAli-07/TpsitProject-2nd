package org.errmili;

import java.io.*;
import java.util.*;

public class Client extends User {

    private double wallet;
    private static final String CLIENT_DIRECTORY = "Clients/"; // Name of the directory

    public Client(String username, String password, double initialWallet) {
        super(username, password);
        this.wallet = initialWallet;
    }

    // Returns the sum of money in your wallet
    public double getWallet() {
        return wallet;
    }

    // Adds money from wallet
    public void addToWallet(double amount) {
        if (amount > 0) {
            wallet += amount;
        }
    }

    // Removes money from wallet
    public boolean removeFromWallet(double amount) {
        if (amount > 0 && amount <= wallet) {
            wallet -= amount;
            return true;
        }
        return false;
    }

    // Prints information
    public void printInfo() {
        System.out.println("Wallet balance for " + getUsername() + ": " + wallet + " EUR");
    }

    // Saving the information to a file
    public void saveToFile() {
        File directory = new File(CLIENT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = CLIENT_DIRECTORY + getUsername() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(getUsername());
            writer.println(getPassword());
            writer.println(wallet);
        } catch (IOException e) {
            System.err.println("Error saving client " + getUsername() + ": " + e.getMessage());
        }
    }

    // Loads the information from a file
    public static Client loadFromFile(String clientName, String password) {
        String filename = CLIENT_DIRECTORY + clientName + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            System.err.println("Client file not found: " + filename);
            return null;
        }

        try (Scanner scanner = new Scanner(file)) {
            String username = scanner.nextLine();
            String storedPassword = scanner.nextLine();
            double wallet = Double.parseDouble(scanner.nextLine());

            // Create client with the stored credentials
            Client client = new Client(username, storedPassword, wallet);

            // Authenticate with provided password
            if (client.authenticate(password)) {
                System.out.println("Client " + username + " loaded successfully.");
                return client;
            } else {
                System.out.println("Authentication failed for client " + username);
                return null;
            }
        } catch (IOException | NoSuchElementException | NumberFormatException e) {
            System.err.println("Error loading client " + clientName + ": " + e.getMessage());
            return null;
        }
    }

    // Gets all the clients name
    public static List<String> getAllClientNames() {
        List<String> clientNames = new ArrayList<>();
        File directory = new File(CLIENT_DIRECTORY);

        if (directory.exists() && directory.isDirectory()) {
            File[] clientFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));

            if (clientFiles != null) {
                for (File file : clientFiles) {
                    clientNames.add(file.getName().replace(".txt", ""));
                }
            }
        }

        return clientNames;
    }
}