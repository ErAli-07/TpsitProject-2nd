#ifndef BANK_H
#define BANK_H

#include <iostream>
#include "Client.h"

using namespace std;

class Bank {
private:
    double balance;
    double currentInvestment;
    bool isInDebt;

public:
    Bank(double initialDeposit); 
    bool deposit(double amount);
    bool withdraw(double amount);
    double getBalance() const;
    bool canInvest() const;
    double investFixed(Client& client, double amount, int months, double interestRate);
    double investWithRisk(Client& client, double amount, int months, double successRate, double returnRate, double lossRate);
    void printInfo() const;
    bool getIsInDebt() const;
    void checkDebtStatus();
};

#endif