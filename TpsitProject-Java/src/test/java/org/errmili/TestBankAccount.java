package org.errmili;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBankAccount {

    @Test
    void testShortTermInvestment() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Create and make a short-term investment
        double investAmount = 500.0;
        Investment shortTermInv = Investment.createFixedInvestment(
                investAmount, 12, 3.0, Investment.SHORT_TERM);

        double accountBalanceBefore = testAccount.getBalance();
        boolean success = InvestmentManager.makeInvestment(testAccount, testInvestments, shortTermInv);

        // Assertions
        assertTrue(success);
        assertEquals(1, testInvestments.size());
        assertEquals(accountBalanceBefore - investAmount, testAccount.getBalance(), 0.001);

        // Process a month
        shortTermInv.processMonth(null); // Modified to not depend on Scanner
        assertTrue(shortTermInv.isActive());

        // Check investment return
        double expectedNewAmount = investAmount * (1 + 0.03/12);
        assertEquals(expectedNewAmount, shortTermInv.getCurrentAmount(), 0.01);
    }

    @Test
    void testLongTermInvestmentFullTerm() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Create a long-term investment
        double investAmount = 1000.0;
        int testDuration = 3;
        Investment longTermInv = Investment.createFixedInvestment(
                investAmount, testDuration, 10.0, Investment.LONG_TERM);

        // Make investment
        assertTrue(InvestmentManager.makeInvestment(testAccount, testInvestments, longTermInv));

        // Process investment
        longTermInv.processMonth(null);
        assertTrue(longTermInv.isActive());
        longTermInv.processMonth(null);
        assertTrue(longTermInv.isActive());

        double finalAmount = longTermInv.processMonth(null);
        assertFalse(longTermInv.isActive());
        assertTrue(finalAmount > investAmount);

        // Verify result
        double monthlyRate = 10.0 / 12.0;
        double expectedFinalAmount = investAmount;
        for (int i = 0; i < testDuration; i++) {
            expectedFinalAmount += (expectedFinalAmount * monthlyRate / 100.0);
        }
        assertEquals(expectedFinalAmount, finalAmount, 0.01);
    }

    @Test
    void testRiskInvestment() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Create risk investment
        double investAmount = 800.0;
        Investment riskInv = Investment.createRiskInvestment(
                investAmount, 1, 100.0, 8.0, 12.0, Investment.MEDIUM_RISK);

        // Make investment
        assertTrue(InvestmentManager.makeInvestment(testAccount, testInvestments, riskInv));

        // Process investment
        double finalAmount = riskInv.processMonth(null);
        assertFalse(riskInv.isActive());

        // Verify return
        double expectedReturn = investAmount * (1 + (8.0/12)/100);
        assertEquals(expectedReturn, finalAmount, 0.01);
    }

    @Test
    void testInvestmentInDebtAccount() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Force account into debt
        testAccount.withdraw(testAccount.getBalance() + 100);
        assertTrue(testAccount.isInDebt());

        // Try to make investment
        Investment investment = Investment.createFixedInvestment(
                500.0, 12, 3.0, Investment.SHORT_TERM);

        // Investment should fail
        assertFalse(InvestmentManager.makeInvestment(testAccount, testInvestments, investment));
        assertEquals(0, testInvestments.size());
    }

    @Test
    void testInsufficientFundsForInvestment() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Try to invest more than available
        double excessAmount = testAccount.getBalance() + 100;
        Investment investment = Investment.createFixedInvestment(
                excessAmount, 12, 3.0, Investment.SHORT_TERM);

        // Investment should fail
        assertFalse(InvestmentManager.makeInvestment(testAccount, testInvestments, investment));
        assertEquals(0, testInvestments.size());
    }

    @Test
    void testProcessMultipleInvestments() {
        // Create test objects
        Client testClient = new Client("testUser", "password123", 5000.0);
        assertTrue(testClient.removeFromWallet(2000.0));
        Account testAccount = new Account(2000.0);
        List<Investment> testInvestments = new ArrayList<>();

        // Create investments
        Investment inv1 = Investment.createFixedInvestment(400.0, 1, 3.0, Investment.SHORT_TERM);
        Investment inv2 = Investment.createRiskInvestment(600.0, 1, 100.0, 5.0, 2.0, Investment.LOW_RISK);

        // Make investments
        assertTrue(InvestmentManager.makeInvestment(testAccount, testInvestments, inv1));
        assertTrue(InvestmentManager.makeInvestment(testAccount, testInvestments, inv2));
        assertEquals(2, testInvestments.size());

        double accountBalanceBefore = testAccount.getBalance();
        double walletBalanceBefore = testClient.getWallet();

        // Process investments
        InvestmentManager.processInvestments(testClient, testAccount, testInvestments, null);

        // Verify results
        assertEquals(0, testInvestments.size());
        assertTrue(testAccount.getBalance() > accountBalanceBefore);
        assertEquals(walletBalanceBefore + 100.0, testClient.getWallet(), 0.001);
    }
}