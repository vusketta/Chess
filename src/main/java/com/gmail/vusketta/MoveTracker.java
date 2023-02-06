package com.gmail.vusketta;

public record MoveTracker(Move move, int moveNumber) {
    public static MoveTracker of(Move move, int moveNumber) {
        return new MoveTracker(move, moveNumber);
    }
}
