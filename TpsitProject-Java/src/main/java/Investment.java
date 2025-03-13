import java.io.*;
import java.util.*;

public class Investment {
    private double amount;
    private int duration;
    private String type;
    private double returnRate;
    private double lossRate;
    private double successRate;
    private boolean isActive;
    private int currentMonth;

    // Constants for investment types
    public static final String SHORT_TERM = "Short Term";
    public static final String MEDIUM_TERM = "Medium Term";
    public static final String LONG_TERM = "Long Term";
    public static final String LOW_RISK = "Low Risk";
    public static final String MEDIUM_RISK = "Medium Risk";
    public static final String HIGH_RISK = "High Risk";

    private Investment(double amount, int duration, String type, double returnRate, double successRate, double lossRate) {
        this.amount = amount;
        this.duration = duration;
        this.type = type;
        this.returnRate = returnRate;
        this.successRate = successRate;
        this.lossRate = lossRate;
        this.isActive = true;
        this.currentMonth = 0;
    }

    // Creates the fixed Investment
    public static Investment createFixedInvestment(double amount, int duration, double returnRate, String type) {
        return new Investment(amount, duration, type, returnRate, 100.0, 0.0);
    }

    // Creates the Investment with a risk amount
    public static Investment createRiskInvestment(double amount, int duration, double successRate, double returnRate, double lossRate, String type) {
        return new Investment(amount, duration, type, returnRate, successRate, lossRate);
    }

    public double getCurrentAmount() {
        return amount;
    }

    // Returns if it's active
    public boolean isActive() {
        return isActive;
    }

    // Makes month pass
    public double processMonth(Scanner scanner) {
        if (!isActive || currentMonth >= duration) {
            return 0.0;
        }

        double oldAmount = amount;
        currentMonth++;

        System.out.println("Month " + currentMonth + " of " + duration);
        System.out.println("Current investment value: " + amount + " EUR");

        System.out.print("Skip month? (y/n): ");
        char choice = scanner.next().charAt(0);
        scanner.nextLine(); // Consume newline

        if (choice != 'y') {
            if (type.equals(SHORT_TERM) || type.equals(MEDIUM_TERM) || type.equals(LONG_TERM)) {
                // Fixed investment
                amount += (returnRate * amount) / 100.0;
                System.out.println("Investment grew by: " + (amount - oldAmount) + " EUR");
            } else {
                // Risk investment
                Random random = new Random();
                int chance = random.nextInt(100) + 1;

                if (chance <= successRate) {
                    amount += (returnRate * amount) / 100.0;
                    System.out.println("Investment succeeded! Gained: " + (amount - oldAmount) + " EUR");
                } else {
                    double loss = (lossRate * amount) / 100.0;
                    amount -= loss;
                    System.out.println("Investment failed! Lost: " + loss + " EUR");
                }
            }
        }

        if (currentMonth >= duration) {
            isActive = false;
            System.out.println("Investment completed!");
            return amount;
        }

        return 0.0;
    }

    // Getters
    public String getType() {
        return type;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public int getDuration() {
        return duration;
    }

    // To store in file format
    public String toFileString() {
        return amount + "," + duration + "," + type + "," + returnRate + "," + successRate + "," + lossRate + "," + isActive + "," + currentMonth;
    }

    // Create from file string
    public static Investment fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 8) return null;

        double amount = Double.parseDouble(parts[0]);
        int duration = Integer.parseInt(parts[1]);
        String type = parts[2];
        double returnRate = Double.parseDouble(parts[3]);
        double successRate = Double.parseDouble(parts[4]);
        double lossRate = Double.parseDouble(parts[5]);
        boolean isActive = Boolean.parseBoolean(parts[6]);
        int currentMonth = Integer.parseInt(parts[7]);

        Investment investment = new Investment(amount, duration, type, returnRate, successRate, lossRate);
        investment.isActive = isActive;
        investment.currentMonth = currentMonth;

        return investment;
    }
}
