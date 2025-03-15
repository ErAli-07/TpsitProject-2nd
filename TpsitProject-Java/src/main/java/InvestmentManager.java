import java.io.*;
import java.util.*;

public class InvestmentManager {
    private static final String INVESTMENT_DIRECTORY = "Investments/"; // Name of the directory

    // Saves investments
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

    // Loads investments
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

    public static void processInvestments(Client client, Account account, List<Investment> investments, Scanner scanner) {
        Iterator<Investment> iterator = investments.iterator();
        while (iterator.hasNext()) {
            Investment investment = iterator.next();
            if (investment.isActive()) {
                double result = investment.processMonth(scanner);
                if (result > 0) {
                    account.deposit(result);
                    iterator.remove();
                    System.out.println("Investment completed and funds returned to account: " + result + " EUR");
                }
            } else {
                iterator.remove();
            }
        }

        // Add 100 euros to the client's wallet after processing investments
        client.addToWallet(100.0);
        System.out.println("Added 100 EUR to your wallet.");
    }
}