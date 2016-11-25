package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author Dmitry
 * @since 23 Nov 2016
 */
public class GoodnessDeterminer {

    //double[] categories = {}
    public static int base = 10;
    public static int delta = 15;

    /*
    * 100 - Royal Flush
    * 90 - 99 - Straight Flush
    * 80 - 89 - Four of a Kind
    * ...
    * 10-19 - pair
    * 0-9 - high card
    * */

    public static int get(List<Card> cards) {
        if (cards.size() == 2) {
            return getCoefficientForTwoCards(cards.get(0), cards.get(1));
        } else {
            return getCoefficient(cards);
        }
    }

    public static int getCoefficient(List<Card> cards) {
        List<List<Card>> fives = permutations(cards, 5);
        List<List<Card>> fours = permutations(cards, 4);
        List<List<Card>> threes = permutations(cards, 3);
        List<List<Card>> pairs = permutations(cards, 2);

        for (List<Card> five : fives) {
            if (royalFlush(five)) return Categories.ROYAL_FLUSH.get(intStream(five), base, delta);
            if (straightFlush(cards)) return Categories.STRAIGHT_FLUSH.get(intStream(five), base, delta);;
        }

        for (List<Card> four : fours) {
            if (ofSameKind(four)) return Categories.FOUR_OF_A_KIND.get(intStream(four), base, delta);;
        }

        for (List<Card> five : fives) {
            if (fullHouse(five)) return Categories.FULL_HOUSE.get(intStream(five), base, delta);;
            if (flush(five)) return Categories.FLUSH.get(intStream(five), base, delta);;
            if (straight(five)) return Categories.STRAIGHT.get(intStream(five), base, delta);;
        }

        for (List<Card> three : threes) {
            if (ofSameKind(three)) return Categories.THREE_OF_A_KIND.get(intStream(three), base, delta);;
        }

        for (List<Card> four : fours) {
            if (twoPairs(four)) return Categories.TWO_PAIR.get(intStream(four), base, delta);;
        }

        for (List<Card> pair : pairs) {
            if (ofSameKind(pair)) return Categories.PAIR.get(intStream(pair), base, delta);;
        }

        return Categories.HIGH_CARD.get(intStream(cards), base, delta);
    }

    public static boolean royalFlush(List<Card> cards) {
        Collections.sort(cards);

        return flush(cards) && straight(cards) && cards.get(0).value == 10;
    }

    public static boolean straightFlush(List<Card> cards) {
        return flush(cards) && straight(cards);
    }

    public static boolean fullHouse(List<Card> cards) {
        if (cards.size() != 5) return false;
        Collections.sort(cards);

        return (ofSameKind(cards.subList(0, 2)) && ofSameKind(cards.subList(2, 5)))
                || (ofSameKind(cards.subList(0, 3)) && ofSameKind(cards.subList(3, 5)));
    }

    public static boolean flush(List<Card> cards) {
        Suit suit = cards.get(0).suit;

        for (Card card : cards) {
            if (card.suit != suit) {
                return false;
            }
        }

        return true;
    }

    public static boolean straight(List<Card> cards) {
        if (cards.size() != 5) return false;

        Collections.sort(cards);
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).value != cards.get(i-1).value + 1) {
                return false;
            }
        }

        return true;
    }

    public static boolean ofSameKind(List<Card> cards) {
        int kind = cards.get(0).value;

        for (Card card : cards) {
            if (card.value != kind) {
                return false;
            }
        }
        return true;
    }

    public static boolean twoPairs(List<Card> cards) {
        if (cards.size() != 4) return false;

        Collections.sort(cards);
        return ofSameKind(cards.subList(0, 2)) && ofSameKind(cards.subList(2, 4));
    }

    public static int getCoefficientForTwoCards(Card one, Card two) {
        if (one.value == two.value) {
            return Categories.PAIR.rank * delta + (int)(delta * one.getRank());
        } else {
            return Categories.HIGH_CARD.rank *  delta + (int) (delta * (one.value > two.value ? one.getRank() : two.getRank()));
        }
    }

    public static List<List<Card>> permutations(List<Card> cards, int k) {
        List<List<Card>> permutations = new ArrayList<>();
        if (k >= cards.size()) {
            permutations.add(cards);
            return permutations;
        }

        int[] perm = IntStream.range(0, k).toArray();

        List<Card> next = get(cards, perm);
        do {
            permutations.add(next);
            next = next(perm, k, cards);
        } while (next != null);


        return permutations;
    }

    public static List<Card> next(int[] perm, int k, List<Card> allCards) {
        int n = allCards.size() - 1;
        for (int i = k - 1; i >= 0; --i) {
            if (perm[i] < n - k + i + 1) {
                perm[i]++;
                for (int j = i + 1; j < k; ++j) {
                    perm[j] = perm[j-1] + 1;
                }

                return get(allCards, perm);
            }
        }
        return null;
    }

    public static List<Card> get(List<Card> cards, int[] perm) {
        List<Card> result = new ArrayList<>();
        for (int i : perm) {
            result.add(cards.get(i));
        }

        return result;
    }

    public static Supplier<IntStream> intStream(List<Card> cards) {
        return () -> IntStream.of(cards.stream().mapToInt(c -> c.value).toArray());
    }

}
