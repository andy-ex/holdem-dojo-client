package main;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry
 * @since 24 Nov 2016
 */
public class CardValue {

    private static Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("1", 1);
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("6", 6);
        put("7", 7);
        put("8", 8);
        put("9", 9);
        put("10", 10);
        put("J", 11);
        put("Q", 12);
        put("K", 13);
        put("A", 14);
    }};

    public static int from(String stringValue) {
        return map.get(stringValue);
    }
}
