package org.example;

public enum BankType {
    NEO(0.01, "НеоКредит Банк"),
    AUM(0.02, "Арум Финтех"),
    VTA(0.00, "Вектор Альянс Банк");

    public final double commissionValue;
    public final String rusName;

    private BankType(double commissionValue, String rusName){
        this.commissionValue = commissionValue;
        this.rusName = rusName;
    }

    public double getCommissionValue() {
        return commissionValue;
    }

    public String getRusName() {
        return rusName;
    }
}
