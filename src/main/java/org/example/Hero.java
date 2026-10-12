package org.example;

abstract public class Hero {
    public static final int MIN_STAT_VALUE = 1;
    private final String name;
    private final int maxHealth;
    private final int baseAttack;
    private int health;

    protected Hero (String name, int maxHealth, int baseAttack){
        this.name = name;
        if (maxHealth < MIN_STAT_VALUE){
            System.out.println("Введено некорректное значение, установлено минимально возможное");
            this.maxHealth = MIN_STAT_VALUE;
            this.health = MIN_STAT_VALUE;
        }
        else {
            this.maxHealth = maxHealth;
            this.health = maxHealth;
        }
        if (baseAttack < MIN_STAT_VALUE){
            System.out.println("Введено некорректное значение, установлено минимально возможное");
            this.baseAttack = MIN_STAT_VALUE;
        }
        else {
            this.baseAttack = baseAttack;
        }
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getHealth() {
        return health;
    }

    public abstract void attack(Hero target);
    public abstract ActionType makeTurn(Hero target);
    void takeDamage(int damage){
        this.health -= damage;
        if (this.health < 0){
            System.out.println(getName() + " пал в бою!");
            this.health = 0;
        }
    }
    void heal(int amount){
        this.health += amount;
        if (getHealth() > maxHealth){
            this.health = maxHealth;
        }
    }

    boolean isAlive(){
        return getHealth() > 0;
    }
}
