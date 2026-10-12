package org.example;

public class Warrior extends Hero implements Castable, Restable {
    private final int armor;

    protected Warrior (String name, int maxHealth, int baseAttack, int armor){
        super(name, maxHealth, baseAttack);
        if (armor > MIN_STAT_VALUE) {
            this.armor = armor;
        }
        else {
            this.armor = MIN_STAT_VALUE;
            System.out.println("Введено некорректное значение, установлено минимально возможное");
        }

    }

    public int getArmor() {
        return armor;
    }

    @Override
    public void takeDamage(int damage){
        int finalDamage = damage - getArmor();
        if (finalDamage < 1){
            finalDamage = 1;
        }
        super.takeDamage(finalDamage);
    }

    @Override
    public void attack(Hero target) {
        System.out.println(getName() + " атакует " + target.getName() + "!");
        target.takeDamage(getBaseAttack());
    }

    @Override
    public ActionType makeTurn(Hero target) {
        if (canCast()){
            castSpecialSkill(target);
            return ActionType.SPECIAL_SKILL;
        }
        else if (needsRest()){
            rest();
            return ActionType.REST;
        }
        else {
            attack(target);
            return ActionType.BASE_ATTACK;
        }
    }


    @Override
    public void castSpecialSkill(Hero target) {
        if (canCast()) {
            target.takeDamage(getBaseAttack() + getArmor() * 2);
        }
        else {
            System.out.println("Воин попытался ударить щитом, но потерял равновесие!");
        }
    }

    @Override
    public boolean canCast() {
        return getHealth() < getMaxHealth() * 0.5;
    }


    @Override
    public void rest() {
        heal(getArmor() * 2);
        System.out.println(getName() + " восстановил " + Integer.toString(getArmor() * 2));
    }

    @Override
    public boolean needsRest() {
        return getHealth() < getMaxHealth() * 0.15;
    }


}
