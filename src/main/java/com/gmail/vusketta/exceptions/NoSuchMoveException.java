package com.gmail.vusketta.exceptions;

import com.gmail.vusketta.Move;

public class NoSuchMoveException extends RuntimeException {

    public NoSuchMoveException(Move move) {
        super("No such move: " + move);
    }
}
