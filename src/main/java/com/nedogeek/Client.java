package com.nedogeek;


import main.BetMaker;
import main.GoodnessDeterminer;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Client {
    private static final String userName = "DY";
    private static final String password = "somePassword";

    private static final String SERVER = "ws://10.6.103.68:8081/ws";
    private org.eclipse.jetty.websocket.WebSocket.Connection connection;

    enum Commands {
        Check, Call, Rise, Fold, AllIn
    }

    class Card {
        final String suit;
        final String value;

        Card(String suit, String value) {
            this.suit = suit;
            this.value = value;
        }
    }


    private void con() {
        WebSocketClientFactory factory = new WebSocketClientFactory();
        try {
            factory.start();

        WebSocketClient client = factory.newWebSocketClient();

        connection = client.open(new URI(SERVER + "?user=" + userName + "&password=" + password), new WebSocket.OnTextMessage() {
            public void onOpen(Connection connection) {
                System.out.println("Opened");
            }

            public void onClose(int closeCode, String message) {

                System.out.println("Closed");
                System.out.println(closeCode + ":" + message);
            }

            public void onMessage(String data) {
                parseMessage(data);
                System.out.println(data);

                if (userName.equals(mover)) {
                    try {
                        doAnswer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Player {

        final String name;
        final int balance;
        final int bet;
        final String status;
        final List<Card> cards;
        Player(String name, int balance, int bet, String status, List<Card> cards) {
            this.name = name;
            this.balance = balance;
            this.bet = bet;
            this.status = status;
            this.cards = cards;
        }

    }
    List<Card> deskCards;

    int pot;
    String gameRound;

    String dealer;
    String mover;
    List<String> event;
    List<Player> players;

    String cardCombination;

    int blind;

    public Client() {
            con();
    }

    public static void main(String[] args) {
        new Client();
    }

    private void parseMessage(String message) {
        JSONObject json = new JSONObject(message);

        if (json.has("deskPot")) {
            pot = json.getInt("deskPot");
        }
        if (json.has("mover")) {
            mover = json.getString("mover");
        }
        if (json.has("dealer")) {
            dealer = json.getString("dealer");
        }
        if (json.has("gameRound")) {
            gameRound = json.getString("gameRound");
        }
        if (json.has("event")) {
            event = parseEvent(json.getJSONArray("event"));
        }
        if (json.has("players")) {
            players = parsePlayers(json.getJSONArray("players"));
        }

        if (json.has("deskCards")) {
            deskCards = parseCards(((JSONArray) json.get("deskCards")));
        }

        if (json.has("combination")) {
            cardCombination = json.getString("combination");
        }
    }

    private List<String> parseEvent(JSONArray eventJSON) {
        List<String> events = new ArrayList<>();

        for (int i = 0; i < eventJSON.length(); i++) {
            events.add(eventJSON.getString(i));
        }

        return events;
    }

    private List<Player> parsePlayers(JSONArray playersJSON) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playersJSON.length(); i++) {
            JSONObject playerJSON = (JSONObject) playersJSON.get(i);
            int balance = 0;
            int bet = 0;
            String status = "";
            String name = "";
            List<Card> cards = new ArrayList<>();

            if (playerJSON.has("balance")) {
                balance = playerJSON.getInt("balance");
            }
            if (playerJSON.has("pot")) {
                bet = playerJSON.getInt("pot");
            }
            if (playerJSON.has("status")) {
                status = playerJSON.getString("status");
            }
            if (playerJSON.has("name")) {
                name = playerJSON.getString("name");
            }
            if (playerJSON.has("cards")) {
                cards = parseCards((JSONArray) playerJSON.get("cards"));
            }

            players.add(new Player(name, balance, bet, status, cards));
        }

        return players;
    }

    private List<Card> parseCards(JSONArray cardsJSON) {
        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < cardsJSON.length(); i++) {
            String cardSuit = ((JSONObject) cardsJSON.get(i)).getString("cardSuit");
            String cardValue = ((JSONObject) cardsJSON.get(i)).getString("cardValue");

            cards.add(new Card(cardSuit, cardValue));
        }

        return cards;
    }

    private void doAnswer() throws IOException {
        this.blind = getSB(players);

        Player me = me(players);

        int toBet = toBet(players) - me.bet;

        List<main.Card> cards = Adapter.getCards(me.cards);
        cards.addAll(Adapter.getCards(deskCards));

        int rate = GoodnessDeterminer.get(cards);

        int currentBet = BetMaker.determBet(rate, gameRound, me.balance, this.blind, me.bet);

        if (BetMaker.shouldRisk) {
            currentBet = BetMaker.agressiveGame(currentBet, toBet, blind);
        }

        if (currentBet - me.bet < blind && !BetMaker.shouldRisk) {
            connection.sendMessage(Commands.Fold.toString());
        }else if (currentBet > this.blind) {
            connection.sendMessage(Commands.Rise.toString() + "," + currentBet);
        } else {
            connection.sendMessage(Commands.Check.toString());
        }
    }

    public static int getSB(List<Player> players) {
        for (Player player : players) {
            if (player.status.equalsIgnoreCase("SmallBlind")) {
                return player.bet;
            }
        }

        return 20;
    }

    public static Player me(List<Player> players) {
        for (Player player : players) {
            if (player.name.equalsIgnoreCase(userName)) {
                return player;
            }
        }

        return null;
    }

    public static int toBet(List<Player> players) {
        int max = 0;
        for (Player player : players) {
            if (player.status.equalsIgnoreCase("Rise")) {
                if (player.bet > max) max = player.bet;
            }
        }

        return max;
    }




}
