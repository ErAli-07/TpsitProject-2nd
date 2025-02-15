import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Investment Bank\n");

        // Initial setup
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter initial wallet amount: ");
        double initialWallet = scanner.nextDouble();

        Client client = new Client(name, initialWallet);

        System.out.print("Enter amount to deposit in bank: ");
        double initialDeposit = scanner.nextDouble();
        if (initialDeposit > initialWallet) {
            System.out.println("Error: Insufficient funds");
            return;
        }

        Bank bank = new Bank(initialDeposit);
        client.removeFromWallet(initialDeposit);

        // Main program loop
        boolean running = true;
        while (running) {
            System.out.println("\n=== Main Menu ===\n" +
                    "1. View account status\n" +
                    "2. Deposit money\n" +
                    "3. Withdraw money\n" +
                    "4. Make investment\n" +
                    "5. Advance time (skip month)\n" +
                    "6. Exit");

            System.out.print("Select option (1-6): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // View status
                    client.printInfo();
                    bank.printInfo();
                    break;

                case 2: // Deposit
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    if (depositAmount > client.getWallet()) {
                        System.out.println("Error: Insufficient funds in wallet");
                        return;
                    }
                    if (bank.deposit(depositAmount)) {
                        client.removeFromWallet(depositAmount);
                        System.out.println("Deposit successful");
                    }
                    break;

                case 3: // Withdraw
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    if (bank.withdraw(withdrawAmount)) {
                        client.addToWallet(withdrawAmount);
                        System.out.println("Withdrawal successful");
                    } else {
                        System.out.println("Error: Insufficient funds in bank");
                    }
                    break;

                case 4: // Investment menu
                    if (bank.getIsInDebt()) {
                        System.out.println("Cannot invest while account is in debt");
                        break;
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
                    if (investChoice == 7) break;

                    System.out.print("Enter investment amount: ");
                    double investAmount = scanner.nextDouble();
                    if (investAmount > bank.getBalance()) {
                        System.out.println("Error: Insufficient funds in bank");
                        break;
                    }

                    switch (investChoice) {
                        case 1:
                            bank.investFixed(client, investAmount, 12, 3.0);
                            break;
                        case 2:
                            bank.investFixed(client, investAmount, 36, 7.0);
                            break;
                        case 3:
                            bank.investFixed(client, investAmount, 60, 10.0);
                            break;
                        case 4:
                            System.out.print("Enter investment duration in months: ");
                            int months4 = scanner.nextInt();
                            bank.investWithRisk(client, investAmount, months4, 95.0, 3.0, 4.0);
                            break;
                        case 5:
                            System.out.print("Enter investment duration in months: ");
                            int months5 = scanner.nextInt();
                            bank.investWithRisk(client, investAmount, months5, 80.0, 8.0, 12.0);
                            break;
                        case 6:
                            System.out.print("Enter investment duration in months: ");
                            int months6 = scanner.nextInt();
                            bank.investWithRisk(client, investAmount, months6, 50.0, 15.0, 30.0);
                            break;
                    }
                    break;

                case 5: // Advance time
                    client.addToWallet(100.0);
                    System.out.println("Advanced one month. Added 100 EUR to wallet.");
                    break;

                case 6: // Exit
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option");
            }
        }

        System.out.println("\nFinal account status:");
        client.printInfo();
        bank.printInfo();

        scanner.close();
    }
}