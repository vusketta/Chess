package com.gmail.vusketta.exceptions;

import com.gmail.vusketta.board.Cell;

public class CellCanNotBeUnderAttack extends RuntimeException {

    public CellCanNotBeUnderAttack(Cell cell) {
        super("Cell can not be under attack: " + cell);
    }
}
