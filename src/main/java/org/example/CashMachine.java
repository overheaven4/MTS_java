package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashMachine implements WithdrawalOperations, DepositOperations {

    @Override
    public double deposit(double balance, double depositAmount) {
        if (depositAmount <= 0) {
            return balance;
        }
        else {
            double newBalance = balance + depositAmount;
            BigDecimal newBalance_bd = BigDecimal.valueOf(newBalance).setScale(2, RoundingMode.HALF_UP);
            return newBalance_bd.doubleValue();
        }
    }

    @Override
    public double withdraw(double balance, double withdrawAmount, BankType bankType) {
        double commission = applyCommission(withdrawAmount, bankType);
        double finSum = withdrawAmount + commission;
        if (balance < finSum){
            System.out.println("Не хватает средств");
            return balance;
        }
        else {
            double newBalance = balance - finSum;
            BigDecimal newBalance_bd = BigDecimal.valueOf(newBalance).setScale(2, RoundingMode.HALF_UP);
            return newBalance_bd.doubleValue();
        }
    }
}
