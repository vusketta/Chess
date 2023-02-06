package com.gmail.vusketta;

public enum Cell {
    WHITE_PAWN(true, false, "p"), BLACK_PAWN(false, true, "P"),
    WHITE_ROOK(true, false, "r"), BLACK_ROOK(false, true, "R"),
    WHITE_KNIGHT(true, false, "n"), BLACK_KNIGHT(false, true, "N"),
    WHITE_BISHOP(true, false, "b"), BLACK_BISHOP(false, true, "B"),
    WHITE_QUEEN(true, false, "q"), BLACK_QUEEN(false, true, "Q"),
    WHITE_KING(true, false, "k"), BLACK_KING(false, true, "K"),
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
