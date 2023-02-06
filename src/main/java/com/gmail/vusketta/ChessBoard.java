package com.gmail.vusketta;

import com.gmail.vusketta.exceptions.NoSuchMoveException;

import java.util.*;
import java.util.stream.Collectors;

public class ChessBoard implements Board, Position {
    private final Cell[][] field;
    private final MoveTracker[][] moveTrackers;
    private final Map<Cell, Coordinate> kingPosition;
    private Turn turn;
    private int moveNumber;

    public ChessBoard() {
        field = new Cell[8][8];
        field[0] = new Cell[]{
                Cell.WHITE_ROOK, Cell.WHITE_KNIGHT, Cell.WHITE_BISHOP, Cell.WHITE_QUEEN,
                Cell.WHITE_KING, Cell.WHITE_BISHOP, Cell.WHITE_KNIGHT, Cell.WHITE_ROOK
        };
        Arrays.fill(field[1], Cell.WHITE_PAWN);
        field[7] = new Cell[]{
                Cell.BLACK_ROOK, Cell.BLACK_KNIGHT, Cell.BLACK_BISHOP, Cell.BLACK_QUEEN,
                Cell.BLACK_KING, Cell.BLACK_BISHOP, Cell.BLACK_KNIGHT, Cell.BLACK_ROOK
        };
        Arrays.fill(field[6], Cell.BLACK_PAWN);
        for (int i = 2; i < 6; i++) {
            Arrays.fill(field[i], Cell.EMPTY);
        }
        moveTrackers = new MoveTracker[8][8];
        for (MoveTracker[] row : moveTrackers) {
            Arrays.fill(row, MoveTracker.of(
                    Move.of(Coordinate.of(-1, -1), Coordinate.of(-1, -1)), 0)
            );
        }
        kingPosition = new HashMap<>();
        kingPosition.put(Cell.WHITE_KING, Coordinate.of(4, 0));
        kingPosition.put(Cell.BLACK_KING, Coordinate.of(4, 7));
        turn = Turn.WHITE;
        moveNumber = 1;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public GameResult makeMove(Move move) {
        if (!isValid(move)) return GameResult.LOSE;

        Cell piece = getCell(move.from());
        if (piece == Cell.WHITE_KING || piece == Cell.BLACK_KING) kingPosition.put(piece, move.to());

        field[move.from().y()][move.from().x()] = Cell.EMPTY;

        Cell temp = field[move.to().y()][move.to().x()];
        if (turn == Turn.WHITE) {
            field[move.to().y()][move.to().x()] =
                    piece == Cell.WHITE_PAWN && move.to().y() == 7 ? Cell.WHITE_QUEEN : piece;
            if (temp.isEmpty() && Math.abs(move.to().x() - move.from().x()) == 1 && piece == Cell.WHITE_PAWN)
                field[4][move.to().x()] = Cell.EMPTY;
        } else {
            field[move.to().y()][move.to().x()] =
                    piece == Cell.BLACK_PAWN && move.to().y() == 0 ? Cell.BLACK_QUEEN : piece;
            if (temp.isEmpty() && Math.abs(move.to().x() - move.from().x()) == 1 && piece == Cell.BLACK_PAWN)
                field[3][move.to().x()] = Cell.EMPTY;
        }

        if (isUnderAttack(kingPosition.get(turn == Turn.WHITE ? Cell.WHITE_KING : Cell.BLACK_KING)))
            return GameResult.LOSE;
        moveTrackers[move.to().y()][move.to().x()] = MoveTracker.of(move, moveNumber);
        turn = turn == Turn.WHITE ? Turn.BLACK : Turn.WHITE;
        moveNumber++;
        return GameResult.UNKNOWN;
    }

    @Override
    public Turn getTurn() {
        return turn;
    }

    private boolean isNotBetween(Move move) throws NoSuchMoveException {
        Coordinate from = move.from();
        final Coordinate to = move.to();
        final int dx = to.x() - from.x();
        final int dy = to.y() - from.y();

        if (dx != 0 && dy == 0) {
            final int start = Math.min(to.x(), from.x()) + 1;
            final int end = Math.max(to.x(), from.x());
            for (int i = start; i < end; i++) {
                if (!getCell(from.y(), i).isEmpty()) return false;
            }
            return true;
        }
        if (dx == 0 && dy != 0) {
            final int start = Math.min(to.y(), from.y()) + 1;
            final int end = Math.max(to.y(), from.y());
            for (int i = start; i < end; i++) {
                if (!getCell(i, from.x()).isEmpty()) return false;
            }
            return true;
        }
        if (Math.abs(dx) == Math.abs(dy)) {
            final int shiftX = (int) Math.signum(dx);
            final int shiftY = (int) Math.signum(dy);
            while (!from.equals(to)) {
                if (!getCell(from.y() + shiftY, from.x() + shiftX).isEmpty()) {
                    return false;
                }
                from = Coordinate.of(from.x() + shiftX, from.y() + shiftY);
            }
            return true;
        }

        throw new NoSuchMoveException(move);
    }

    @Override
    public boolean isValid(Move move) {
        final Coordinate from = move.from();
        final Coordinate to = move.to();

        if (!(0 <= from.x() && from.x() < 8 && 0 <= from.y() && from.y() < 8)) return false;
        if (!(0 <= to.x() && to.x() < 8 && 0 <= to.y() && to.y() < 8)) return false;

        final Cell fromPiece = getCell(from);
        if (turn == Turn.WHITE && fromPiece.isBlack()) {
            return false;
        }
        if (turn == Turn.BLACK && fromPiece.isWhite()) {
            return false;
        }
        final Cell toPiece = getCell(to);
        if (toPiece == Cell.WHITE_KING || toPiece == Cell.BLACK_KING)
            return false;

        final int dx = to.x() - from.x();
        final int dy = to.y() - from.y();

        final boolean whitePawnMove = toPiece.isEmpty() && dx == 0 && (dy == 1 || from.y() == 1 && dy == 2);
        final boolean whitePawnAttack = toPiece.isBlack() && (dx == 1 && dy == 1 || dx == -1 && dy == 1);
        final boolean whiteEnPassant = toPiece.isEmpty() && from.y() == 4 && to.y() == 5 &&
                Math.abs(dx) == 1 && getCell(4, to.x()) == Cell.BLACK_PAWN &&
                moveTrackers[4][to.x()].moveNumber() == moveNumber - 1 &&
                moveTrackers[4][to.x()].move().equals(Move.of(Coordinate.of(to.x(), 6), Coordinate.of(to.x(), 4)));
        final boolean blackPawnMove = toPiece.isEmpty() && dx == 0 && (dy == -1 || from.y() == 6 && dy == -2);
        final boolean blackPawnAttack = toPiece.isWhite() && (dx == 1 && dy == -1 || dx == -1 && dy == -1);
        final boolean blackEnPassant = toPiece.isEmpty() && from.y() == 3 && to.y() == 2 &&
                Math.abs(dx) == 1 && getCell(3, to.x()) == Cell.WHITE_PAWN &&
                moveTrackers[3][to.x()].moveNumber() == moveNumber - 1 &&
                moveTrackers[3][to.x()].move().equals(Move.of(Coordinate.of(to.x(), 1), Coordinate.of(to.x(), 3)));
        final boolean kingMove = Math.abs(dx) == 1 && Math.abs(dy) == 0 || Math.abs(dx) == 0 && Math.abs(dy) == 1 ||
                Math.abs(dx) == 1 && Math.abs(dy) == 1;
        final boolean rookMove = (dx != 0 && dy == 0 || dx == 0 && dy != 0) && isNotBetween(move);
        final boolean knightMove = Math.abs(dx) == 1 && Math.abs(dy) == 2 || Math.abs(dx) == 2 && Math.abs(dy) == 1;
        final boolean bishopMove = Math.abs(dx) == Math.abs(dy) && isNotBetween(move);
        final boolean queenMove = rookMove || bishopMove;
        return switch (fromPiece) {
            case WHITE_PAWN -> whitePawnMove || whitePawnAttack || whiteEnPassant;
            case BLACK_PAWN -> blackPawnMove || blackPawnAttack || blackEnPassant;
            case WHITE_ROOK -> rookMove && !toPiece.isWhite();
            case BLACK_ROOK -> rookMove && !toPiece.isBlack();
            case WHITE_KNIGHT -> knightMove && !toPiece.isWhite();
            case BLACK_KNIGHT -> knightMove && !toPiece.isBlack();
            case WHITE_BISHOP -> bishopMove && !toPiece.isWhite();
            case BLACK_BISHOP -> bishopMove && !toPiece.isBlack();
            case WHITE_QUEEN -> queenMove && !toPiece.isWhite();
            case BLACK_QUEEN -> queenMove && !toPiece.isBlack();
            case WHITE_KING -> kingMove && !toPiece.isWhite();
            case BLACK_KING -> kingMove && !toPiece.isBlack();
            case EMPTY -> false;
        };
    }

    @Override
    public boolean isUnderAttack(Coordinate coordinate) {
        final int x = coordinate.x();
        final int y = coordinate.y();
        if (getCell(y, x).isWhite()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (getCell(j, i).isWhite() || getCell(j, i).isEmpty()) continue;
                    if (isValid(Move.of(Coordinate.of(i, j), Coordinate.of(x, y)))) return true;
                }
            }
        }
        if (getCell(y, x).isBlack()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (getCell(j, i).isBlack() || getCell(j, i).isEmpty()) continue;
                    if (isValid(Move.of(Coordinate.of(i, j), Coordinate.of(x, y)))) return true;
                }
            }
        }
        if (getCell(y, x).isEmpty()) throw new IllegalArgumentException("Empty cell can not be attack");
        return false;
    }

    @Override
    public List<Move> possibleMoves(Coordinate coordinate) {
        List<Move> moves = new ArrayList<>();
        final Cell piece = getCell(coordinate);
        final int x = coordinate.x();
        final int y = coordinate.y();
        switch (piece) {
            case WHITE_PAWN -> {
                moves.add(Move.of(coordinate, Coordinate.of(x, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x, y + 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, 5)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, 5)));
            }
            case BLACK_PAWN -> {
                moves.add(Move.of(coordinate, Coordinate.of(x, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x, y - 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, 2)));
            }
            case WHITE_ROOK, BLACK_ROOK -> {
                for (int i = -7; i < 8; i++) {
                    if (i == 0) continue;
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y)));
                    moves.add(Move.of(coordinate, Coordinate.of(x, y + i)));
                }
            }
            case WHITE_KNIGHT, BLACK_KNIGHT -> {
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y + 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y - 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y - 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y + 2)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 2, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 2, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 2, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 2, y - 1)));
            }
            case WHITE_BISHOP, BLACK_BISHOP -> {
                for (int i = -7; i < 8; i++) {
                    if (i == 0) continue;
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y + i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x - i, y + i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y - i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x - i, y - i)));
                }
            }
            case WHITE_QUEEN, BLACK_QUEEN -> {
                for (int i = -7; i < 8; i++) {
                    if (i == 0) continue;
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y)));
                    moves.add(Move.of(coordinate, Coordinate.of(x, y + i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y + i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x - i, y + i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x + i, y - i)));
                    moves.add(Move.of(coordinate, Coordinate.of(x - i, y - i)));
                }
            }
            case WHITE_KING, BLACK_KING -> {
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y)));
                moves.add(Move.of(coordinate, Coordinate.of(x + 1, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x, y - 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y + 1)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y)));
                moves.add(Move.of(coordinate, Coordinate.of(x - 1, y - 1)));
            }
            case EMPTY -> {
            }
        }
        return moves.stream().filter(this::isValid).collect(Collectors.toList());
    }

    private List<Move> allPossibleMoves() {
        List<Move> allMoves = new ArrayList<>();
        List<Coordinate> pieces = getPieceCoordinates();
        for (Coordinate piece : pieces) {
            allMoves.addAll(possibleMoves(piece));
        }
        return allMoves;
    }

    @Override
    public Cell getCell(int row, int column) {
        return field[row][column];
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        return getCell(coordinate.y(), coordinate.x());
    }

    @Override
    public List<Coordinate> getPieceCoordinates() {
        List<Coordinate> pieces = new ArrayList<>();
        if (Turn.WHITE == turn) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Coordinate coordinate = Coordinate.of(i, j);
                    if (getCell(coordinate).isWhite()) pieces.add(coordinate);
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Coordinate coordinate = Coordinate.of(i, j);
                    if (getCell(coordinate).isBlack()) pieces.add(coordinate);
                }
            }
        }
        return pieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("  a b c d e f g h");
        sb.append(System.lineSeparator());

        for (int i = 1; i <= 8; i++) {
            sb.append(9 - i).append(" ");
            for (Cell cell : field[8 - i]) {
                sb.append(cell).append(" ");
            }
            sb.append(9 - i).append(System.lineSeparator());
        }

        sb.append("  a b c d e f g h");
        return sb.toString();
    }
}

/*
 6) Попробовать изучить правила, сделать рокировку.
*/
