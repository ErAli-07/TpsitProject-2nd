#include <iostream>
#include "Bank.h"

using namespace std;

Bank::Bank(double initialDeposit) {
    balance = initialDeposit;
    currentInvestment = 0.0;
    isInDebt = false;
}

bool Bank::deposit(double amount) {
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

bool Bank::withdraw(double amount) {
    if (amount <= 0 || amount > balance) return false;
    balance -= amount;
    return true;
}

double Bank::getBalance() const {
    return balance;
}

bool Bank::canInvest() const {
    return balance >= 0;
}

double Bank::investFixed(Client& client, double amount, int months, double interestRate) {
    if (!canInvest() || amount > balance || amount <= 0 || isInDebt) {
        cout << "Invalid investment amount or account in debt" << endl;
        return 0;
    }

    balance -= amount;
    currentInvestment = amount;
    double originalInvestment = amount;

    cout << "Investment duration: " << months << " months" << endl;
    cout << "Initial investment: " << originalInvestment << " EUR" << endl;

    for (int i = 0; i < months; i++) {
        client.addToWallet(100.0);

        cout << "Month " << (i + 1) << " of " << months << endl;
        cout << "Current investment value: " << currentInvestment << " EUR" << endl;
        cout << "Skip month? (y/n)" << endl;
        char choice;
        cin >> choice;
        if (choice == 'y') continue;

        currentInvestment += (interestRate * currentInvestment) / 100.0;

        double monthlyChange = currentInvestment - originalInvestment;
        cout << "Current profit/loss: " << monthlyChange << " EUR" << endl;
    }

    double finalAmount = currentInvestment;
    double totalReturn = finalAmount - originalInvestment;
    cout << "Investment completed!" << endl;
    cout << "Total return: " << totalReturn << " EUR" << endl;

    currentInvestment = 0;
    balance += finalAmount;
    checkDebtStatus();
    return finalAmount;
}

double Bank::investWithRisk(Client& client, double amount, int months,
    double successRate, double returnRate, double lossRate) {
    if (!canInvest() || amount > balance || amount <= 0 || isInDebt) {
        cout << "Invalid investment amount or account in debt" << endl;
        return 0;
    }

    balance -= amount;
    currentInvestment = amount;
    double originalInvestment = amount; 

    cout << "Risk investment details:" << endl;
    cout << "Success rate: " << successRate << "%" << endl;
    cout << "Return rate on success: " << returnRate << "%" << endl;
    cout << "Loss rate on failure: " << lossRate << "%" << endl;

    for (int i = 0; i < months; i++) {
        client.addToWallet(100.0);

        cout << "Month " << (i + 1) << " of " << months << endl;
        cout << "Current investment value: " << currentInvestment << " EUR" << endl;
        cout << "Skip month? (y/n)" << endl;
        char choice;
        cin >> choice;
        if (choice == 'y') continue;

        int chance = (rand() % 100) + 1;
        double oldValue = currentInvestment;

        if (chance <= successRate) {
            currentInvestment += (returnRate * currentInvestment) / 100.0;
            cout << "Investment succeeded! Gained: " <<
                (currentInvestment - oldValue) << " EUR" << endl;
        }
        else {
            currentInvestment -= (lossRate * currentInvestment) / 100.0;
            cout << "Investment failed! Lost: " <<
                (oldValue - currentInvestment) << " EUR" << endl;
        }

        double monthlyChange = currentInvestment - originalInvestment;
        cout << "Current profit/loss: " << monthlyChange << " EUR" << endl;
    }

    double finalAmount = currentInvestment;
    double totalReturn = finalAmount - originalInvestment;
    cout << "Investment completed!" << endl;
    cout << "Total return: " << totalReturn << " EUR" << endl;

    currentInvestment = 0;
    balance += finalAmount;
    checkDebtStatus();
    return finalAmount;
}

bool Bank::getIsInDebt() const {
    return isInDebt;
}

void Bank::checkDebtStatus() {
    isInDebt = (balance < 0);
}

void Bank::printInfo() const {
    cout << "Bank balance: " << balance << " EUR" << endl;
    if (currentInvestment > 0) {
        cout << "Current investment: " << currentInvestment << " EUR" << endl;
    }
    if (isInDebt) {
        cout << "Account is in debt. New investments are blocked until debt is repaid." << endl;
    }
}