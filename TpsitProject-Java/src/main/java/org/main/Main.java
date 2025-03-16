package org.main;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try{
            consoleInterface();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void consoleInterface() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Investment Bank\n");

        Client currentClient = null;
        Account currentAccount = null;
        List<Investment> currentInvestments = null;

        boolean running = true;
        while (running) {
            if (currentClient == null) {
                System.out.println("\n=== Client Management ===\n" +
                        "1. Create new client\n" +
                        "2. Load existing client\n" +
                        "3. Load all clients\n" +
                        "4. Exit");

                System.out.print("Select option (1-4): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter client name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter initial wallet amount: ");
                        double initialWallet = scanner.nextDouble();
                        scanner.nextLine();

                        currentClient = new Client(name, password, initialWallet);
                        System.out.print("Enter amount to deposit in account: ");
                        double initialDeposit = scanner.nextDouble();
                        scanner.nextLine(); 

                        if (initialDeposit > initialWallet) {
                            System.out.println("Error: Insufficient funds");
                            currentClient = null;
                        } else {
                            if (currentClient.removeFromWallet(initialDeposit)) {
                                currentAccount = new Account(initialDeposit);
                                currentInvestments = new ArrayList<>();
                                currentClient.saveToFile();
                                currentAccount.saveToFile(currentClient.getUsername());
                                InvestmentManager.saveInvestments(currentClient.getUsername(), currentInvestments);
                                System.out.println("Client created and saved successfully.");
                            } else {
                                System.out.println("Error: Could not remove funds from wallet");
                                currentClient = null;
                            }
                        }
                        break;

                    case 2:
                        System.out.print("Enter client name to load: ");
                        String clientName = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String loadPassword = scanner.nextLine();

                        currentClient = Client.loadFromFile(clientName, loadPassword);
                        if (currentClient != null) {
                            currentAccount = Account.loadFromFile(clientName);
                            currentInvestments = InvestmentManager.loadInvestments(clientName);
                        }
                        break;

                    case 3:
                        List<String> clientNames = Client.getAllClientNames();
                        if (clientNames.size() > 0) {
                            System.out.println("\nAvailable clients:");
                            for (int i = 0; i < clientNames.size(); i++) {
                                System.out.println((i + 1) + ". " + clientNames.get(i));
                            }

                            System.out.print("Select client (1-" + clientNames.size() + "): ");
                            int clientIndex = scanner.nextInt();
                            scanner.nextLine(); // Consume newline

                            if (clientIndex > 0 && clientIndex <= clientNames.size()) {
                                String selectedName = clientNames.get(clientIndex - 1);
                                System.out.print("Enter password: ");
                                String selectedPassword = scanner.nextLine();

                                currentClient = Client.loadFromFile(selectedName, selectedPassword);
                                if (currentClient != null) {
                                    currentAccount = Account.loadFromFile(selectedName);
                                    currentInvestments = InvestmentManager.loadInvestments(selectedName);
                                }
                            }
                        } else {
                            System.out.println("No clients found.");
                        }
                        break;

                    case 4:
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option");
                }
            } else {
                System.out.println("\n=== Main Menu (" + currentClient.getUsername() + ") ===\n" +
                        "1. View account status\n" +
                        "2. Deposit money\n" +
                        "3. Withdraw money\n" +
                        "4. Make investment\n" +
                        "5. Process investments\n" +
                        "6. Advance time (skip month)\n" +
                        "7. Save client data\n" +
                        "8. Switch client\n" +
                        "9. Exit");

                System.out.print("Select option (1-9): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        currentClient.printInfo();
                        currentAccount.printInfo();
                        System.out.println("Active investments: " + countActiveInvestments(currentInvestments));
                        break;

                    case 2:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        if (depositAmount > currentClient.getWallet()) {
                            System.out.println("Error: Insufficient funds in wallet");
                        } else if (currentClient.removeFromWallet(depositAmount)) {
                            currentAccount.deposit(depositAmount);
                            System.out.println("Deposit successful");
                            saveAllData(currentClient, currentAccount, currentInvestments);
                        }
                        break;

                    case 3:
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        if (currentAccount.withdraw(withdrawAmount)) {
                            currentClient.addToWallet(withdrawAmount);
                            System.out.println("Withdrawal successful");
                            saveAllData(currentClient, currentAccount, currentInvestments);
                        } else {
                            System.out.println("Error: Insufficient funds in account");
                        }
                        break;

                    case 4:
                        handleInvestmentMenu(scanner, currentClient, currentAccount, currentInvestments);
                        break;

                    case 5:
                        InvestmentManager.processInvestments(currentClient, currentAccount, currentInvestments, scanner);
                        saveAllData(currentClient, currentAccount, currentInvestments);
                        break;

                    case 6:
                        currentClient.addToWallet(100.0);
                        System.out.println("Advanced one month. Added 100 EUR to wallet.");
                        saveAllData(currentClient, currentAccount, currentInvestments);
                        break;

                    case 7:
                        saveAllData(currentClient, currentAccount, currentInvestments);
                        System.out.println("All client data saved.");
                        break;

                    case 8:
                        saveAllData(currentClient, currentAccount, currentInvestments);
                        currentClient = null;
                        currentAccount = null;
                        currentInvestments = null;
                        System.out.println("Logged out. Returning to client selection.");
                        break;

                    case 9:
                        saveAllData(currentClient, currentAccount, currentInvestments);
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option");
                }
            }
        }

        System.out.println("\nThank you for using the Investment Bank!");
        scanner.close();
    }

    private static void saveAllData(Client client, Account account, List<Investment> investments) {
        client.saveToFile();
        account.saveToFile(client.getUsername());
        InvestmentManager.saveInvestments(client.getUsername(), investments);
    }

    private static int countActiveInvestments(List<Investment> investments) {
        int count = 0;
        for (Investment investment : investments) {
            if (investment.isActive()) {
                count++;
            }
        }
        return count;
    }

    private static void handleInvestmentMenu(Scanner scanner, Client client, Account account, List<Investment> investments) {
        if (account.isInDebt()) {
            System.out.println("Cannot invest while account is in debt");
            return;
        }

        System.out.println("\nInvestment Options:\n" +
                "1. Short term (1 year, 3% interest)\n" +
                "2. Medium term (3 years, 7% interest)\n" +
                "3. Long term (5 years, 10% interest)\n" +
                "4. Low risk (95% success, 3% return, 4% loss)\n" +
                "5. Medium risk (80% success, 8% return, 12% loss)\n" +
                "6. High risk (50% success, 15% return, 30% loss)\n" +
                "7. Return to main menu");

        System.out.print("Select investment type (1-7): ");
        int investChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (investChoice == 7) return;

        System.out.print("Enter investment amount: ");
        double investAmount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (investAmount > account.getBalance()) {
            System.out.println("Error: Insufficient funds in account");
            return;
        }

        Investment investment = null;

        switch (investChoice) {
            case 1:
                investment = Investment.createFixedInvestment(
                        investAmount, 12, 3.0, Investment.SHORT_TERM);
                break;
            case 2:
                investment = Investment.createFixedInvestment(
                        investAmount, 36, 7.0, Investment.MEDIUM_TERM);
                break;
            case 3:
                investment = Investment.createFixedInvestment(
                        investAmount, 60, 10.0, Investment.LONG_TERM);
                break;
            case 4:
                System.out.print("Enter investment duration in months: ");
                int months4 = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                investment = Investment.createRiskInvestment(
                        investAmount, months4, 95.0, 3.0, 4.0, Investment.LOW_RISK);
                break;
            case 5:
                System.out.print("Enter investment duration in months: ");
                int months5 = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                investment = Investment.createRiskInvestment(
                        investAmount, months5, 80.0, 8.0, 12.0, Investment.MEDIUM_RISK);
                break;
            case 6:
                System.out.print("Enter investment duration in months: ");
                int months6 = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                investment = Investment.createRiskInvestment(
                        investAmount, months6, 50.0, 15.0, 30.0, Investment.HIGH_RISK);
                break;
        }

        if (investment != null) {
            if (InvestmentManager.makeInvestment(account, investments, investment)) {
                System.out.println("Investment created successfully!");
            } else {
                System.out.println("Failed to create investment.");
            }
        }

        saveAllData(client, account, investments);
    }
}