package main;

/**
 * @author Dmitry
 * @since 23 Nov 2016
 */
public class Card implements Comparable {

    public Suit suit;
    public int value;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public double getRank() {
        return this.value / 14.0;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Card) {
            return this.value - ((Card)o).value;
        }

        return 0;
    }

    @Override
    public String toString() {
        return this.value + ":" + this.suit.name();
    }
}
