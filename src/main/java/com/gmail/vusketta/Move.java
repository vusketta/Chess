package com.gmail.vusketta;

public record Move(Coordinate from, Coordinate to) {

    public static Move of(Coordinate from, Coordinate to) {
        return new Move(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            return ((Move) o).from.equals(from) && ((Move) o).to.equals(to);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Move(from = " + from + ", to = " + to + ")";
    }
}
