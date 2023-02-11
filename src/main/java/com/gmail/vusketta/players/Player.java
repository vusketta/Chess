package com.gmail.vusketta.players;

import com.gmail.vusketta.Move;
import com.gmail.vusketta.Position;

public interface Player {
    Move makeMove(Position position);
}
