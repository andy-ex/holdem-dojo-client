package com.nedogeek;

import main.Card;
import main.CardValue;
import main.Suit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmitry
 * @since 24 Nov 2016
 */
public class Adapter {

    public static List<Card> getCards(List<Client.Card> cards) {

        return cards.stream().map(card -> new main.Card(Suit.from(card.suit), CardValue.from(card.value))).collect(Collectors.toList());
    }
}
