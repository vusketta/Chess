package com.gmail.vusketta;

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
        return "(" + x + ", " + y + ")";
    }
}
