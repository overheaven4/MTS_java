package org.example;

import java.util.Locale;

public class Account {
    public int cardNumber, pinCode;
    public double balance;
    public BankType bankType;

    public Account(int cardNumber, int pinCode, double balance, BankType bankType) {
        if (10000 <= cardNumber && cardNumber <= 99999){
            this.cardNumber = cardNumber;
        }
        else {
            this.cardNumber = 10000;
        }
        if (100 <= pinCode && pinCode <= 999){
            this.pinCode = pinCode;
        }
        else {
            this.pinCode = 100;
        }
        if (balance >= 0) {
            this.balance = balance;
        }
        else {
            this.balance = 0;
        }
        if (bankType == null){
            this.bankType = BankType.NEO;
        }
        else {
            this.bankType = bankType;
        }
    }

    public BankType getBankType() {
        return bankType;
    }

    public double getBalance() {
        return balance;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getPinCode() {
        return pinCode;
    }

    public String toString() {
        return String.format(Locale.US, "%s Карта: %d, Баланс: %.2f руб.",
                this.bankType.getRusName(), this.cardNumber, this.balance);
    }
}
