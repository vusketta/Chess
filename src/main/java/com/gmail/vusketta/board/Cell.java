package com.gmail.vusketta.board;

public enum Cell {
    WHITE_PAWN(true, false, "P"), BLACK_PAWN(false, true, "p"),
    WHITE_ROOK(true, false, "R"), BLACK_ROOK(false, true, "r"),
    WHITE_KNIGHT(true, false, "N"), BLACK_KNIGHT(false, true, "n"),
    WHITE_BISHOP(true, false, "B"), BLACK_BISHOP(false, true, "b"),
    WHITE_QUEEN(true, false, "Q"), BLACK_QUEEN(false, true, "q"),
    WHITE_KING(true, false, "K"), BLACK_KING(false, true, "k"),
    EMPTY(false, false, ".");

    private final boolean isWhite;
    private final boolean isBlack;
    private final String emoji;

    Cell(boolean isWhite, boolean isBlack, String emoji) {
        this.isWhite = isWhite;
        this.isBlack = isBlack;
        this.emoji = emoji;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public boolean isEmpty() {
        return !isWhite && !isBlack;
    }

    @Override
    public String toString() {
        return emoji;
    }
}
