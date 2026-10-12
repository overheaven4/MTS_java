package org.example;

public class Arena {
    private final Hero fighter1;
    private final Hero fighter2;

    public Arena(Hero fighter1, Hero fighter2) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
    }

    public void startTour() {
        int round = 1;
        System.out.println("=== Битва начинается: " + fighter1.getName() + " против " + fighter2.getName() + " ===");

        while (fighter1.isAlive() && fighter2.isAlive()) {
            System.out.println("\n--- Раунд " + round + " ---");

            Hero attacker;
            Hero defender;

            if (Math.random() < 0.5) {
                attacker = fighter1;
                defender = fighter2;
            } else {
                attacker = fighter2;
                defender = fighter1;
            }

            playTurn(attacker, defender);

            if (defender.isAlive()) {
                playTurn(defender, attacker);
            }

            round++;
        }

        Hero winner = fighter1.isAlive() ? fighter1 : fighter2;
        System.out.println("\n=== Бой окончен! Победитель: " + winner.getName() + " ===");
    }

    private void playTurn(Hero attacker, Hero defender) {
        ActionType action = attacker.makeTurn(defender);

        System.out.println("-> Итог хода: " + attacker.getName() + " выполнил действие: [" + action.getRusName() + "]");
        System.out.println("-> Здоровье " + defender.getName() + ": " + defender.getHealth() + " / " + defender.getMaxHealth());
    }
}