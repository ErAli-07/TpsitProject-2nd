package org.errmili;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class InvestmentManager {

    private static final String INVESTMENT_DIRECTORY = "Investments/"; // Name of the directory

    // Saves the data of an Investment into a file, then after an Investment is finished the data is removed
    public static void saveInvestments(String username, List<Investment> investments) {
        File directory = new File(INVESTMENT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = INVESTMENT_DIRECTORY + username + "_investments.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Investment investment : investments) {
                writer.println(investment.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving investment data for " + username + ": " + e.getMessage());
        }
    }

    // Loads an Investment from a file
    public static List<Investment> loadInvestments(String username) {
        List<Investment> investments = new ArrayList<>();
        String filename = INVESTMENT_DIRECTORY + username + "_investments.txt";
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("No existing investment data found for " + username);
            return investments;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Investment investment = Investment.fromFileString(line);
                if (investment != null) {
                    investments.add(investment);
                }
            }
            System.out.println("Investment data for " + username + " loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading investments: " + e.getMessage());
        }

        return investments;
    }

    // Makes investments
    public static boolean makeInvestment(Account account, List<Investment> investments, Investment investment) {
        if (account.isInDebt() || account.getBalance() < investment.getCurrentAmount()) {
            return false;
        }

        account.withdraw(investment.getCurrentAmount());
        investments.add(investment);
        return true;
    }

    // Process investments and creates a file where there is the transaction history of investment
    public static void processInvestments(Client client, Account account, List<Investment> investments, Scanner scanner) {
        File directory = new File(INVESTMENT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String logFilename = INVESTMENT_DIRECTORY + client.getUsername() + "_investment_log.txt";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        try (PrintWriter logWriter = new PrintWriter(new FileWriter(logFilename, true))) { // append mode
            logWriter.println("\n--- Investment Processing for " + client.getUsername() + " on " + timestamp + " ---");

            // Use iterator to safely remove completed investments during iteration
            Iterator<Investment> iterator = investments.iterator();
            while (iterator.hasNext()) {
                Investment investment = iterator.next();
                if (investment.isActive()) {
                    logWriter.println("\nProcessing " + investment.getType() + " investment:");
                    logWriter.println("Month " + investment.getCurrentMonth() + " of " + investment.getDuration());
                    logWriter.println("Current investment value: " + String.format("%.2f", investment.getCurrentAmount()) + " EUR");

                    StringWriter consoleOutput = new StringWriter();
                    PrintWriter consoleCapture = new PrintWriter(consoleOutput);
                    PrintStream originalOut = System.out;

                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream captureStream = new PrintStream(baos);
                        System.setOut(captureStream);

                        // Process the investment
                        double result = investment.processMonth(scanner);

                        // Get the captured output
                        String output = baos.toString();
                        originalOut.print(output);
                        logWriter.print(output);

                        if (result > 0) {
                            account.deposit(result);
                            // Important: Remove the investment from the list since it's completed
                            iterator.remove();
                            String completionMsg = "Investment completed and funds returned to account: " + result + " EUR";
                            originalOut.println(completionMsg);
                            logWriter.println(completionMsg);
                        }
                    } finally {
                        // Restore original System.out
                        System.setOut(originalOut);
                    }
                } else {
                    // Remove inactive investments
                    iterator.remove();
                }
            }

            // Add 100 euros to the client's wallet after processing investments
            client.addToWallet(100.0);
            String walletMsg = "Added 100 EUR to your wallet.";
            System.out.println(walletMsg);
            logWriter.println(walletMsg);

            logWriter.println("--- End of Investment Processing ---");

        } catch (IOException e) {
            System.err.println("Error writing to investment log file: " + logFilename + " (" + e.getMessage() + ")");
        }
    }
}