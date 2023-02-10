package com.gmail.vusketta;

import java.util.Map;

public class BoardUtils {
    public static boolean inside(Coordinate coordinate) {
        final int x = coordinate.x();
        final int y = coordinate.y();
        return 0 <= x && x < 8 && 0 <= y && y < 8;
    }

    public static Map<Character, Integer> getNotation() {
        return Map.of(
                'a', 1, 'b', 2, 'c', 3, 'd', 4,
                'e', 5, 'f', 6, 'g', 7, 'h', 8
        );
    }

    public static Map<Integer, Character> getReversedNotation() {
        return Map.of(
                1, 'a', 2, 'b', 3, 'c', 4, 'd', 5,
                'e', 6, 'f', 7, 'g', 8, 'h'
        );
    }
}
