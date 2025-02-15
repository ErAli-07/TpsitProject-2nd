public class Client extends Thread {

    private String name;
    private double wallet;

    Client(String clientName, double initialWallet) {
        this.name = clientName;
        this.wallet = initialWallet;
    }

    public final double getWallet() {
        return wallet;
    }

    public final String getUsername(){
        return name;
    }

    public final void printInfo(){
        System.out.println("Wallet balance for " + name + ": " + wallet + " EUR");
    }

    public void addToWallet(double amount) {
        if (amount > 0) {
            wallet += amount;
        }
    }

    public void removeFromWallet(double amount) {
        if (amount > 0 && amount <= wallet) {
            wallet -= amount;
        }
    }

    @Override
    public void run() {}
}