#include <iostream>
#include "Client.h"

using namespace std;

Client::Client(const string& clientName, double initialWallet) {
    name = clientName;
    wallet = initialWallet;
}

void Client::addToWallet(double amount) {
    if (amount > 0) {
        wallet += amount;
    }
}

void Client::removeFromWallet(double amount) {
    if (amount > 0 && amount <= wallet) {
        wallet -= amount;
    }
}

double Client::getWallet() const {
    return wallet;
}

string Client::getName() const {
    return name;
}

void Client::printInfo() const {
    cout << "Wallet balance for " << name << ": " << wallet << " EUR" << endl;
}