package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface WithdrawalOperations {
    public double withdraw(double balance, double withdrawAmount, BankType bankType);
    default public double applyCommission(double withdrawAmount, BankType bankType){
        if (bankType == null){
            return 0;
        }
        else {
            double commission = withdrawAmount * bankType.getCommissionValue();
            BigDecimal commission_bd = BigDecimal.valueOf(commission).setScale(2, RoundingMode.HALF_UP);
            return commission_bd.doubleValue();
        }
    }
}
