package com.gmail.vusketta.board;

import com.gmail.vusketta.GameResult;
import com.gmail.vusketta.Move;
import com.gmail.vusketta.Position;

public interface Board {
    Position getPosition();
    GameResult makeMove(Move move, boolean doNotSkipGameResult);
}
