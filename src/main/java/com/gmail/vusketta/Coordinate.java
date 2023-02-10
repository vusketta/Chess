package com.gmail.vusketta;

import java.util.Map;

public record Coordinate(int x, int y) {

    public static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Coordinate) {
            return ((Coordinate) o).x == x && ((Coordinate) o).y == y;
        }
        return false;
    }

    @Override
    public String toString() {
        Map<Integer, Character> notation = BoardUtils.getReversedNotation();
        return String.valueOf(notation.get(x + 1)) + (y + 1);
    }
}
