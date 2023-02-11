package com.gmail.vusketta;

import com.gmail.vusketta.board.Cell;

import java.util.List;

public interface Position {
    Turn getTurn();
    boolean isValid(Move move);
    boolean isUnderAttack(Coordinate coordinate);
    List<Move> possibleMoves(Coordinate coordinate);
    Cell getCell(int row, int column);
    Cell getCell(Coordinate coordinate);
    List<Coordinate> getPieceCoordinates();
    String getFen();
}