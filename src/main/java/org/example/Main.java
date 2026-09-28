package org.example;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Account account = new Account(12345, 999, 10000.00, BankType.AUM);
        System.out.println("Здравствуйте! Необходима авторизация");
        System.out.println("Введите номер карты:");
        int cardNumber;
        if (scanner.hasNextInt()){
            cardNumber = scanner.nextInt();
        }
        else {
            System.out.println("Введены некорректные данные");
            return;
        }
        System.out.println("Введите пин-код");
        int pinCode;
        if (scanner.hasNextInt()){
            pinCode = scanner.nextInt();
        }
        else {
            System.out.println("Введены некорректные данные");
            return;
        }
        if (pinCode != account.getPinCode() || cardNumber != account.getCardNumber()){
            System.out.println("Ошибка доступа");
            return;
        }
        else {
            System.out.println("Добро пожаловать!");
            CashMachine cashMachine = new CashMachine();

            double depositAmount;
            System.out.println("Введите сумму для внесения:");
            if (scanner.hasNextDouble()){
                depositAmount = scanner.nextDouble();
            }
            else {
                System.out.println("Введены некорректные данные");
                return;
            }
            account.balance = cashMachine.deposit(account.getBalance(), depositAmount);
            System.out.print("[Статус] ");
            System.out.println(account.toString());

            double withdrawAmount;
            System.out.println("Введите сумму для снятия:");
            if (scanner.hasNextDouble()){
                withdrawAmount = scanner.nextDouble();
            }
            else {
                System.out.println("Введены некорректные данные");
                return;
            }
            account.balance = cashMachine.withdraw(account.getBalance(), withdrawAmount, account.getBankType());
            System.out.print("[Статус] ");
            System.out.println(account.toString());
        }
    }
}