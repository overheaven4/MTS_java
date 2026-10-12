package org.example;

public class Main {
    public static void main(String[] args) {
        Warrior warrior = new Warrior("Артур", 100, 15, 5);
        Mage mage = new Mage("Мерлин", 70, 10, 50);
        Arena arena = new Arena(warrior, mage);
        arena.startTour();
    }
}