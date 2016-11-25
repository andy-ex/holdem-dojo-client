package main;

/**
 * @author Dmitry
 * @since 23 Nov 2016
 */
public enum Suit {
    HEART("♥"), DIAMOND("♦"), CLUB("♣"), SPADE("♠");

    private String suit;

    Suit(String suit) {
        this.suit = suit;
    }

    public  static Suit from(String stringSuit) {
        for (Suit suit : Suit.values()) {
            if (suit.suit.equals(stringSuit)) return suit;
        }

        return null;
    }

}
