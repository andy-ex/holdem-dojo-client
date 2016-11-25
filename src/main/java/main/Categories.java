package main;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author Dmitry
 * @since 23 Nov 2016
 */
public enum Categories {
    HIGH_CARD(0, s -> (s.get().sum() - Categories.MIN_CARD * s.get().count()) / (Categories.CARD_BASE * s.get().count())),
    PAIR(1, s -> (s.get().findFirst().getAsInt() - Categories.MIN_CARD) / Categories.CARD_BASE),
    TWO_PAIR(2, s -> (s.get().sum() - Categories.MIN_CARD * s.get().count()) / (Categories.CARD_BASE * s.get().count())),
    THREE_OF_A_KIND(3, s -> (s.get().findFirst().getAsInt() - Categories.MIN_CARD) / Categories.CARD_BASE),
    STRAIGHT(4, s -> (s.get().max().getAsInt() - Categories.MIN_CARD) / Categories.CARD_BASE),
    FLUSH(5, s -> (s.get().sum() - Categories.MIN_CARD * s.get().count()) / (Categories.CARD_BASE * s.get().count())),
    FULL_HOUSE(6, s -> (s.get().sum() - Categories.MIN_CARD * s.get().count()) / (Categories.CARD_BASE * s.get().count())),
    FOUR_OF_A_KIND(7, s -> (s.get().findFirst().getAsInt() - Categories.MIN_CARD)/ Categories.CARD_BASE),
    STRAIGHT_FLUSH(8, s -> (s.get().max().getAsInt() - Categories.MIN_CARD) / Categories.CARD_BASE),
    ROYAL_FLUSH(9, s -> 1.0);

    private static final double MIN_CARD = 2.0;
    private static final double CARD_BASE = 12.0;

    public int rank;
    public Function<Supplier<IntStream>, Double> goodness;

    Categories(int rank, Function<Supplier<IntStream>, Double> f) {
        this.rank = rank;
        this.goodness = f;
    }
    public int get(Supplier<IntStream> stream, int base, int delta) {
        return rank * base + (int) Math.round(delta * goodness.apply(stream));
    }
}
