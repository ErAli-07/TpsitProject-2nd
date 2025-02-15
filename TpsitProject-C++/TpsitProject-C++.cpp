#include <iostream>
#include <string>
#include "Bank.h"

using namespace std;

int main() {
    cout << "Welcome to the Investment Bank\n\n";

    // Initial setup
    cout << "Enter your name: ";
    string name;
    getline(cin, name);

    double initialWallet;
    cout << "Enter initial wallet amount: ";
    cin >> initialWallet;

    Client client(name, initialWallet);

    double initialDeposit;
    cout << "Enter amount to deposit in bank: ";
    cin >> initialDeposit;
    if (initialDeposit > initialWallet) {
        cout << "Error: Insufficient funds\n";
        return 1;
    }

    Bank bank(initialDeposit);
    client.removeFromWallet(initialDeposit);

    // Main program loop
    bool running = true;
    while (running) {
        cout << "\n=== Main Menu ===\n"
            << "1. View account status\n"
            << "2. Deposit money\n"
            << "3. Withdraw money\n"
            << "4. Make investment\n"
            << "5. Advance time (skip month)\n"
            << "6. Exit\n";

        int choice;
        cout << "Select option (1-6): ";
        cin >> choice;

        switch (choice) {
        case 1: // View status
            client.printInfo();
            bank.printInfo();
            break;

        case 2: // Deposit
        {
            double amount;
            cout << "Enter amount to deposit: ";
            cin >> amount;
            if (amount > client.getWallet()) {
                cout << "Error: Insufficient funds in wallet\n";
                return -1;
            }
            if (bank.deposit(amount)) {
                client.removeFromWallet(amount);
                cout << "Deposit successful\n";
            }
        }
        break;

        case 3: { // Withdraw
            double amount;
            cout << "Enter amount to withdraw: ";
            cin >> amount;
            if (bank.withdraw(amount)) {
                client.addToWallet(amount);
                cout << "Withdrawal successful\n";
            }
            else {
                cout << "Error: Insufficient funds in bank\n";
            }
            break;
        }

        case 4: { // Investment menu
            if (bank.getIsInDebt()) {
                cout << "Cannot invest while account is in debt\n";
                break;
            }

            cout << "\nInvestment Options:\n"
                << "1. Short term (1 year, 3% interest)\n"
                << "2. Medium term (3 years, 7% interest)\n"
                << "3. Long term (5 years, 10% interest)\n"
                << "4. Low risk (95% success, 3% return, 4% loss)\n"
                << "5. Medium risk (80% success, 8% return, 12% loss)\n"
                << "6. High risk (50% success, 15% return, 30% loss)\n"
                << "7. Return to main menu\n";

            int investChoice;
            cout << "Select investment type (1-7): ";
            cin >> investChoice;
            if (investChoice == 7) break;

            double investAmount;
            cout << "Enter investment amount: ";
            cin >> investAmount;
            if (investAmount > bank.getBalance()) {
                cout << "Error: Insufficient funds in bank\n";
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
            case 4: {
                int months;
                cout << "Enter investment duration in months: ";
                cin >> months;
                bank.investWithRisk(client, investAmount, months, 95.0, 3.0, 4.0);
                break;
            }
            case 5: {
                int months;
                cout << "Enter investment duration in months: ";
                cin >> months;
                bank.investWithRisk(client, investAmount, months, 80.0, 8.0, 12.0);
                break;
            }
            case 6: {
                int months;
                cout << "Enter investment duration in months: ";
                cin >> months;
                bank.investWithRisk(client, investAmount, months, 50.0, 15.0, 30.0);
                break;
            }
            }
            break;
        }

        case 5: // Advance time
            client.addToWallet(100.0);
            cout << "Advanced one month. Added 100 EUR to wallet.\n";
            break;

        case 6: // Exit
            running = false;
            break;

        default:
            cout << "Invalid option\n";
        }
    }

    cout << "\nFinal account status:\n";
    client.printInfo();
    bank.printInfo();

    return 0;
}