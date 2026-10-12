package org.example;

public class Mage extends Hero implements Castable, Restable{
    private final int maxMana;
    private int mana;

    protected Mage(String name, int maxHealth, int baseAttack, int maxMana) {
        super(name, maxHealth, baseAttack);
        if (maxMana > MIN_STAT_VALUE) {
            this.maxMana = maxMana;
            this.mana = maxMana;
        }
        else {
            this.maxMana = MIN_STAT_VALUE;
            this.mana = MIN_STAT_VALUE;
            System.out.println("Введено некорректное значение, установлено минимально возможное");
        }
    }

    @Override
    public void castSpecialSkill(Hero target) {
        System.out.println(getName() + " использует огненную глыбу!");
        if (getMana() < 25){
            System.out.println("Заклинание отменено - не хватает маны");
        }
        else {
            this.mana -= 25;
            if (getMana() < 0){
                this.mana = 0;
            }
            target.takeDamage(getBaseAttack() * 3);
        }
    }

    @Override
    public boolean canCast() {
        return getMana() >= 25;
    }

    @Override
    public void attack(Hero target) {
        if (getMana() >= 10){
            target.takeDamage(getBaseAttack() * 2);
            System.out.println(getName() + " использует магический выстрел на " + target.getName() + "!");
        }
        else {
            target.takeDamage(getBaseAttack() / 2);
            this.mana += 5;
            if (getMana() > getMaxMana()){
                this.mana = getMaxMana();
            }
            System.out.println(getName() + " использует удар посохом на " + target.getName() + " и восстанавливает 5 маны!");
        }
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
    public void rest() {
        this.mana = getMaxMana();
        super.heal(getBaseAttack());
        System.out.println(getName() + " использует исцеляющую медитацию");
    }

    @Override
    public boolean needsRest() {
        return (getMana() == 0) || (getHealth() < getMaxHealth() * 0.3);
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }
}
