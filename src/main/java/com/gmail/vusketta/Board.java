package com.gmail.vusketta;

public interface Board {
    Position getPosition();
    GameResult makeMove(Move move, boolean doNotSkipGameResult);
}
