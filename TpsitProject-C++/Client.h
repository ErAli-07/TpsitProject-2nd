#ifndef CLIENT_H
#define CLIENT_H

#include <iostream>
#include <string>

using namespace std;

class Client {
private:
    string name;
    double wallet;

public:
    Client(const string& clientName, double initialWallet);
    void addToWallet(double amount);
    void removeFromWallet(double amount);
    double getWallet() const;
    string getName() const;
    void printInfo() const;
};

#endif