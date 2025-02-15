import java.util.Scanner;
import java.util.Random;

public class Bank {

    private double balance;
    private double currentInvestment;
    private boolean isInDebt;

    Bank(double initialDeposit){
        this.balance = initialDeposit;
        this.currentInvestment = 0.0;
        this.isInDebt = false;
    }

    public final double getBalance() {
        return balance;
    }

    public final boolean canInvest(){
        return balance >= 0;
    }

    public final boolean getIsInDebt(){
        return isInDebt;
    }

    public final void checkDebtStatus(){
        isInDebt = (balance < 0);
    }

    public boolean deposit(double amount){
        if (amount <= 0) return false;

        if (balance < 0) {
            double remainingDebt = -balance;
            if (amount >= remainingDebt) {
                balance = amount - remainingDebt;
                isInDebt = false;
            }
            else {
                balance += amount;
            }
        }
        else {
            balance += amount;
        }
        checkDebtStatus();
        return true;
    }

    public boolean withdraw(double amount){
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    public final void printInfo(){
        System.out.println("Bank balance: " + balance + " EUR");
        if (currentInvestment > 0) {
            System.out.println("Current investment value: " + currentInvestment + " EUR");
        }
        if (isInDebt) {
            System.out.println("Account is in debt. New investments are blocked until debt is repaid.");
        }
    }

    public double investFixed(Client client, double amount, int months, double interestRate){
        if (!canInvest() || amount > balance || amount <= 0 || isInDebt) {
            System.out.println("Invalid investment amount or account in debt");
            return 0;
        }

        balance -= amount;
        currentInvestment = amount;
        double originalInvestment = amount;

        System.out.println("Investment duration: " + months + " months");
        System.out.println("Initial investment: " + originalInvestment + " EUR");

        for (int i = 0; i < months; i++) {
            client.addToWallet(100.0);

            System.out.println("Month " + (i + 1) + " of " + months);
            System.out.println("Current investment value: " + currentInvestment + " EUR");
            System.out.println("Skip month? (y/n) ");
            Scanner scanner = new Scanner(System.in);
            char choice;
            choice = scanner.next().charAt(0);
            if (choice == 'y') continue;

            currentInvestment += (interestRate * currentInvestment) / 100.0;

            double monthlyChange = currentInvestment - originalInvestment;
            System.out.println("Current profit/loss: " + monthlyChange + " EUR");
        }

        double finalAmount = currentInvestment;
        double totalReturn = finalAmount - originalInvestment;
        System.out.println("Investment completed!");
        System.out.println("Total return: " + totalReturn + " EUR");

        currentInvestment = 0;
        balance += finalAmount;
        checkDebtStatus();
        return finalAmount;
    }

    public double investWithRisk(Client client, double amount, int months, double successRate, double returnRate, double lossRate){
        if (!canInvest() || amount > balance || amount <= 0 || isInDebt) {
            System.out.println("Invalid investment amount or account in debt");
            return 0;
        }

        balance -= amount;
        currentInvestment = amount;
        double originalInvestment = amount;

        System.out.println("Risk investment details:");
        System.out.println("Success rate: " + successRate + "%");
        System.out.println("Return rate on success: " + returnRate + "%");
        System.out.println("Loss rate on failure: " + lossRate + "%");

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        for (int i = 0; i < months; i++) {
            client.addToWallet(100.0);

            System.out.println("Month " + (i + 1) + " of " + months);
            System.out.println("Current investment value: " + currentInvestment + " EUR");
            System.out.println("Skip month? (y/n)");
            char choice = scanner.next().charAt(0);
            if (choice == 'y') continue;

            int chance = random.nextInt(100) + 1;
            double oldValue = currentInvestment;

            if (chance <= successRate) {
                currentInvestment += (returnRate * currentInvestment) / 100.0;
                System.out.println("Investment succeeded! Gained: " + (currentInvestment - oldValue) + " EUR");
            } else {
                currentInvestment -= (lossRate * currentInvestment) / 100.0;
                System.out.println("Investment failed! Lost: " + (oldValue - currentInvestment) + " EUR");
            }

            double monthlyChange = currentInvestment - originalInvestment;
            System.out.println("Current profit/loss: " + monthlyChange + " EUR");
        }

        double finalAmount = currentInvestment;
        double totalReturn = finalAmount - originalInvestment;
        System.out.println("Investment completed!");
        System.out.println("Total return: " + totalReturn + " EUR");

        currentInvestment = 0;
        balance += finalAmount;
        checkDebtStatus();
        return finalAmount;
    }
}