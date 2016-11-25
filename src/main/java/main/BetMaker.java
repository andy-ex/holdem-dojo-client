package main;

/**
 * @author Yury
 */


public class BetMaker {

    public static boolean shouldRisk = false;

    public static int determBet(int cardsRate, String gameRound, int balance, int blind, int currentBet) {
        switch (gameRound) {
            case "BLIND":
                shouldRisk(cardsRate, balance);
                return blind(cardsRate, balance, blind);

            case "THREE_CARDS":
                return roundBet(blind, currentBet, flop(cardsRate, balance, blind));

            case "FOUR_CARDS":
                return roundBet(blind, currentBet, turn(cardsRate, balance, blind));

            case "FIVE_CARDS":
                return roundBet(blind, currentBet, river(cardsRate, balance, blind));
        }

        return 0;

    }

    private static int roundBet(int blind, int currentBet, int bet) {
        if (bet >= currentBet + blind) {
            return bet;
        } else {
            return 0;
        }
    }

    private static int blind(int cardsRate, int balance, int blind) {
        if (cardsRate < 10) {
            return 0;
        }

        if (cardsRate >= 15) {
            return balance * cardsRate / 100;
        }

        return Math.min(3 * blind, balance * cardsRate / 100);
    }

    private static int flop(int cardsRate, int balance, int blind) {
        if (cardsRate >= 25) {
            return balance * cardsRate / 100;
        }

        return Math.min(3 * blind, balance * cardsRate / 100);
    }

    private static int turn(int cardsRate, int balance, int blind) {
        if (cardsRate >= 30) {
            return balance * cardsRate / 100;
        }

        return Math.max(3 * blind, balance * cardsRate / 100);
    }

    private static int river(int cardsRate, int balance, int blind) {
        if (cardsRate >= 30) {
            return balance * cardsRate / 100;
        }

        return Math.max(3 * blind, balance * cardsRate / 100);
    }


    public static boolean acceptRize(int cardsRate, String gameRound) {
        return shouldRisk || cardsRate > getRate(gameRound);
    }

    private static int getRate(String gameRound) {
        switch (gameRound) {
            case "BLIND":
                return 15;

            case "THREE_CARDS":
                return 20;

            case "FOUR_CARDS":
                return 25;

            case "FIVE_CARDS":
                return 30;
        }

        return 30;
    }


    public static final int RISK_FACTOR = 10;
    public static final int START_AMOUNT = 1000;

    public static void shouldRisk(int cardsRate, int balance) {
        if (cardsRate > 15) {
            shouldRisk = Math.random() < balance / (10 * START_AMOUNT);
        }

        shouldRisk = false;
    }

    public static int agressiveGame(int bet, int delta, int blind) {
        if (2 * blind > delta && delta > bet) {
            return bet + 2 * blind;
        } else {
            return bet;
        }
    }

}
