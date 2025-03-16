package org.errmili;

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

    // Skips months and fluctuates the capital
    public double processMonth(Scanner scanner) {
        if (!isActive) {
            return 0.0;
        }

        currentMonth++;
        System.out.println("\nProcessing " + type + " investment:");
        System.out.println("Month " + currentMonth + " of " + duration);
        System.out.println("Current investment value: " + String.format("%.2f", amount) + " EUR");

        double oldAmount = amount;

        // Different processing for fixed vs risk investments
        if (type.equals(SHORT_TERM) || type.equals(MEDIUM_TERM) || type.equals(LONG_TERM)) {
            // Fixed investment - simple monthly interest
            double monthlyRate = returnRate / 12.0;
            double monthlyInterest = (monthlyRate * amount) / 100.0;
            amount += monthlyInterest;
            System.out.println("Monthly interest rate: " + String.format("%.2f", monthlyRate) + "%");
            System.out.println("Investment grew by: " + String.format("%.2f", monthlyInterest) + " EUR");
        } else {
            // Risk investment - chance of success/failure
            System.out.println("Success rate: " + successRate + "%");
            System.out.println("Return rate if successful: " + returnRate + "%");
            System.out.println("Loss rate if failed: " + lossRate + "%");

            Random random = new Random();
            double roll = random.nextDouble() * 100;
            System.out.println("Rolling chance (need < " + successRate + "): " + String.format("%.2f", roll));

            if (roll < successRate) {
                // Success - apply monthly return rate
                double monthlyReturn = returnRate / 12.0;
                double gain = (monthlyReturn * amount) / 100.0;
                amount += gain;
                System.out.println("SUCCESS! Gained: " + String.format("%.2f", gain) + " EUR");
            } else {
                // Failure - apply monthly loss rate
                double monthlyLoss = lossRate / 12.0;
                double loss = (monthlyLoss * amount) / 100.0;
                amount -= loss;
                System.out.println("FAILURE! Lost: " + String.format("%.2f", loss) + " EUR");
            }
        }

        System.out.println("New investment value: " + String.format("%.2f", amount) + " EUR");

        // Check if investment is complete
        if (currentMonth >= duration) {
            isActive = false;
            System.out.println("Investment duration completed!");
            return amount; // Return final amount
        }

        return 0.0; // Investment still active
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
