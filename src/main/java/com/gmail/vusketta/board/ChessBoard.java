package com.gmail.vusketta.board;

import com.gmail.vusketta.*;
import com.gmail.vusketta.exceptions.CellCanNotBeUnderAttack;

import java.util.*;
import java.util.stream.Collectors;

import static com.gmail.vusketta.board.BoardUtils.*;

public class ChessBoard implements Board, Position {
    private final Cell[][] field;
    private final MoveTracker[][] moveTrackers;
    private final Map<Cell, Coordinate> kingPosition;
    private Turn turn;
    private int moveNumber, draw50MovesRule;
    private boolean isPawnMoved, isPieceTaken;
    private final boolean[] isRoqueNotUsed;
    private Coordinate enPassant;

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
        draw50MovesRule = 0;
        isPawnMoved = isPieceTaken = false;
        isRoqueNotUsed = new boolean[]{true, true, true, true};
        enPassant = null;
    }

    private ChessBoard(final ChessBoard chessBoard) {
        field = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(chessBoard.field[i], 0, field[i], 0, 8);
        }
        moveTrackers = new MoveTracker[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(chessBoard.moveTrackers[i], 0, moveTrackers[i], 0, 8);
        }
        kingPosition = new HashMap<>();
        kingPosition.put(Cell.WHITE_KING, chessBoard.kingPosition.get(Cell.WHITE_KING));
        kingPosition.put(Cell.BLACK_KING, chessBoard.kingPosition.get(Cell.BLACK_KING));
        turn = chessBoard.turn;
        moveNumber = chessBoard.moveNumber;
        draw50MovesRule = chessBoard.draw50MovesRule;
        isPawnMoved = chessBoard.isPawnMoved;
        isPieceTaken = chessBoard.isPieceTaken;
        isRoqueNotUsed = new boolean[4];
        System.arraycopy(chessBoard.isRoqueNotUsed, 0, isRoqueNotUsed, 0, 4);
        enPassant = chessBoard.enPassant;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public GameResult makeMove(final Move move, final boolean doNotSkipGameResult) {
        final Cell piece = getCell(move.from());
        final Cell temp = getCell(move.to());
        if (piece == Cell.WHITE_KING || piece == Cell.BLACK_KING) kingPosition.put(piece, move.to());

        killCell(move, move.from());

        final int dx = move.to().x() - move.from().x();
        final int dy = move.to().y() - move.from().y();
        enPassant = null;

        if (turn == Turn.WHITE) {
            changeCell(move, move.to(), piece == Cell.WHITE_PAWN && move.to().y() == 7 ? Cell.WHITE_QUEEN : piece);
            if (piece == Cell.WHITE_PAWN && dy == 2) enPassant = move.to();
            if (piece == Cell.WHITE_PAWN && temp.isEmpty() && Math.abs(dx) == 1)
                killCell(move, Coordinate.of(move.to().x(), 4));
            if (piece == Cell.WHITE_KING && Math.abs(dx) == 2) {
                isRoqueNotUsed[dx == 2 ? 0 : 1] = false;
                final Coordinate rookFrom = dx == 2 ? Coordinate.of(7, 0) : Coordinate.of(0, 0);
                final Coordinate rookTo = dx == 2 ? Coordinate.of(5, 0) : Coordinate.of(3, 0);
                final Move rookMove = Move.of(rookFrom, rookTo);
                changeCell(rookMove, rookTo, Cell.WHITE_ROOK);
                killCell(rookMove, rookFrom);
            }
        } else {
            changeCell(move, move.to(), piece == Cell.BLACK_PAWN && move.to().y() == 0 ? Cell.BLACK_QUEEN : piece);
            if (piece == Cell.BLACK_PAWN && dy == -2) enPassant = move.to();
            if (piece == Cell.BLACK_PAWN && temp.isEmpty() && Math.abs(dx) == 1)
                killCell(move, Coordinate.of(move.to().x(), 3));
            if (piece == Cell.BLACK_KING && Math.abs(dx) == 2) {
                isRoqueNotUsed[dx == 2 ? 2 : 3] = false;
                final Coordinate rookFrom = dx == 2 ? Coordinate.of(7, 7) : Coordinate.of(0, 7);
                final Coordinate rookTo = dx == 2 ? Coordinate.of(5, 7) : Coordinate.of(3, 7);
                final Move rookMove = Move.of(rookFrom, rookTo);
                changeCell(rookMove, rookTo, Cell.BLACK_ROOK);
                killCell(rookMove, rookFrom);
            }
        }

        draw50MovesRule = isPieceTaken || isPawnMoved ? 0 : draw50MovesRule + 1;
        isPieceTaken = isPawnMoved = false;

        if (doNotSkipGameResult && draw50MovesRule == 50) return GameResult.DRAW;

        moveNumber++;
        turn = turn == Turn.WHITE ? Turn.BLACK : Turn.WHITE;

        final Cell king = turn == Turn.WHITE ? Cell.WHITE_KING : Cell.BLACK_KING;
        if (doNotSkipGameResult && allPossibleMoves().isEmpty()) {
            return isUnderAttack(kingPosition.get(king)) ? GameResult.WIN : GameResult.DRAW;
        }

        return GameResult.UNKNOWN;
    }

    @Override
    public Turn getTurn() {
        return turn;
    }

    private boolean isNotBetween(final Move move) {
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

        return false;
    }

    @Override
    public boolean isValid(final Move move) {
        final Coordinate from = move.from();
        final Coordinate to = move.to();

        if (!inside(from) || !inside(to)) return false;

        final Cell fromPiece = getCell(from);
        if (turn == Turn.WHITE && !fromPiece.isWhite()) {
            return false;
        }
        if (turn == Turn.BLACK && !fromPiece.isBlack()) {
            return false;
        }

        final Cell toPiece = getCell(to);
        if (toPiece == Cell.WHITE_KING || toPiece == Cell.BLACK_KING)
            return false;

        final int dx = to.x() - from.x();
        final int dy = to.y() - from.y();

        final boolean whitePawnMove = toPiece.isEmpty() && dx == 0 && (dy == 1 || from.y() == 1 && dy == 2) && isNotBetween(move);
        final boolean whitePawnAttack = toPiece.isBlack() && (dx == 1 && dy == 1 || dx == -1 && dy == 1);
        final boolean whiteEnPassant = toPiece.isEmpty() && from.y() == 4 && to.y() == 5 &&
                Math.abs(dx) == 1 && getCell(4, to.x()) == Cell.BLACK_PAWN &&
                moveTrackers[4][to.x()].moveNumber() == moveNumber - 1 &&
                moveTrackers[4][to.x()].move().equals(Move.of(Coordinate.of(to.x(), 6), Coordinate.of(to.x(), 4)));
        final boolean blackPawnMove = toPiece.isEmpty() && dx == 0 && (dy == -1 || from.y() == 6 && dy == -2) && isNotBetween(move);
        final boolean blackPawnAttack = toPiece.isWhite() && (dx == 1 && dy == -1 || dx == -1 && dy == -1);
        final boolean blackEnPassant = toPiece.isEmpty() && from.y() == 3 && to.y() == 2 &&
                Math.abs(dx) == 1 && getCell(3, to.x()) == Cell.WHITE_PAWN &&
                moveTrackers[3][to.x()].moveNumber() == moveNumber - 1 &&
                moveTrackers[3][to.x()].move().equals(Move.of(Coordinate.of(to.x(), 1), Coordinate.of(to.x(), 3)));
        final boolean whiteRoque = (dx == 2 && getCell(0, 7) == Cell.WHITE_ROOK && isNotMoved(Coordinate.of(7, 0)) ||
                dx == -2 && getCell(0, 0) == Cell.WHITE_ROOK && isNotMoved(Coordinate.of(0, 0))) &&
                dy == 0 && isNotBetween(move) && isNotMoved(move.from());
        final boolean blackRoque = (dx == 2 && getCell(7, 7) == Cell.BLACK_ROOK && isNotMoved(Coordinate.of(7, 7)) ||
                dx == -2 && getCell(7, 0) == Cell.BLACK_ROOK && isNotMoved(Coordinate.of(0, 7))) &&
                dy == 7 && isNotBetween(move) && isNotMoved(move.from());
        final boolean pieceMove = switch (fromPiece) {
            case WHITE_PAWN -> whitePawnMove || whitePawnAttack || whiteEnPassant;
            case BLACK_PAWN -> blackPawnMove || blackPawnAttack || blackEnPassant;
            case WHITE_ROOK -> checkRookMove(dx, dy) && isNotBetween(move) && !toPiece.isWhite();
            case BLACK_ROOK -> checkRookMove(dx, dy) && isNotBetween(move) && !toPiece.isBlack();
            case WHITE_KNIGHT -> checkKnightMove(dx, dy) && !toPiece.isWhite();
            case BLACK_KNIGHT -> checkKnightMove(dx, dy) && !toPiece.isBlack();
            case WHITE_BISHOP -> checkBishopMove(dx, dy) && isNotBetween(move) && !toPiece.isWhite();
            case BLACK_BISHOP -> checkBishopMove(dx, dy) && isNotBetween(move) && !toPiece.isBlack();
            case WHITE_QUEEN -> checkQueenMove(dx, dy) && isNotBetween(move) && !toPiece.isWhite();
            case BLACK_QUEEN -> checkQueenMove(dx, dy) && isNotBetween(move) && !toPiece.isBlack();
            case WHITE_KING -> (checkKingMove(dx, dy) || whiteRoque) && !toPiece.isWhite() &&
                    checkPieceIsNotNearKing(to, kingPosition.get(turn == Turn.WHITE ? Cell.BLACK_KING : Cell.WHITE_KING));
            case BLACK_KING -> (checkKingMove(dx, dy) || blackRoque) && !toPiece.isBlack() &&
                    checkPieceIsNotNearKing(to, kingPosition.get(turn == Turn.WHITE ? Cell.BLACK_KING : Cell.WHITE_KING));
            case EMPTY -> false;
        };

        return pieceMove && isNotCheckAfterMove(move);
    }

    @Override
    public boolean isUnderAttack(final Coordinate coordinate) {
        final Cell piece = getCell(coordinate);
        if (piece.isEmpty()) throw new CellCanNotBeUnderAttack(piece);

        final int x = coordinate.x();
        final int y = coordinate.y();

        final Cell pawn = piece.isWhite() ? Cell.BLACK_PAWN : Cell.WHITE_PAWN;
        final Cell rook = piece.isWhite() ? Cell.BLACK_ROOK : Cell.WHITE_ROOK;
        final Cell knight = piece.isWhite() ? Cell.BLACK_KNIGHT : Cell.WHITE_KNIGHT;
        final Cell bishop = piece.isWhite() ? Cell.BLACK_BISHOP : Cell.WHITE_BISHOP;
        final Cell queen = piece.isWhite() ? Cell.BLACK_QUEEN : Cell.WHITE_QUEEN;

        final Coordinate pawn1 = Coordinate.of(x - 1, piece.isWhite() ? y + 1 : y - 1);
        final Coordinate pawn2 = Coordinate.of(x + 1, piece.isWhite() ? y + 1 : y - 1);
        if (inside(pawn1) && getCell(pawn1) == pawn) return true;
        if (inside(pawn2) && getCell(pawn2) == pawn) return true;

        final List<Coordinate> rooks = getRookAttacks(x, y);
        for (Coordinate rook1 : rooks) {
            if (inside(rook1) && (getCell(rook1) == rook || getCell(rook1) == queen)
                    && isNotBetween(Move.of(rook1, coordinate)))
                return true;
        }

        final List<Coordinate> knights = getKnightAttacks(x, y);
        for (Coordinate knight1 : knights) {
            if (inside(knight1) && getCell(knight1) == knight) return true;
        }

        final List<Coordinate> bishops = getBishopAttacks(x, y);
        for (Coordinate bishop1 : bishops) {
            if (inside(bishop1) && (getCell(bishop1) == bishop || getCell(bishop1) == queen)
                    && isNotBetween(Move.of(bishop1, coordinate))) return true;
        }

        return false;
    }

    private boolean isNotCheckAfterMove(final Move move) {
        final ChessBoard temp = new ChessBoard(this);
        temp.makeMove(move, false);
        final Coordinate king = temp.kingPosition.get(temp.turn == Turn.WHITE ? Cell.BLACK_KING : Cell.WHITE_KING);
        return !temp.isUnderAttack(king);
    }

    @Override
    public List<Move> possibleMoves(final Coordinate coordinate) {
        assert (inside(coordinate));
        final Cell piece = getCell(coordinate);
        return getPieceMoves(piece, coordinate)
                .stream()
                .filter(this::isValid)
                .collect(Collectors.toList());
    }

    private List<Move> allPossibleMoves() {
        List<Move> allMoves = new ArrayList<>();
        List<Coordinate> pieces = getPieceCoordinates();
        for (Coordinate piece : pieces) allMoves.addAll(possibleMoves(piece));
        return allMoves;
    }

    @Override
    public Cell getCell(final int row, final int column) {
        assert (inside(Coordinate.of(column, row)));
        return field[row][column];
    }

    @Override
    public Cell getCell(final Coordinate coordinate) {
        return getCell(coordinate.y(), coordinate.x());
    }

    private void changeCell(final Move move, final Coordinate coordinate, final Cell cell) {
        if (!getCell(move.to()).isEmpty()) isPieceTaken = true;
        final int x = coordinate.x();
        final int y = coordinate.y();
        field[y][x] = cell;
        moveTrackers[y][x] = MoveTracker.of(move, moveNumber);
        isPawnMoved = cell == Cell.WHITE_PAWN || cell == Cell.BLACK_PAWN;
    }

    private void killCell(final Move move, final Coordinate coordinate) {
        changeCell(move, coordinate, Cell.EMPTY);
    }

    private boolean isNotMoved(final Coordinate coordinate) {
        assert (inside(coordinate));
        final int x = coordinate.x();
        final int y = coordinate.y();
        return moveTrackers[y][x].equals(
                MoveTracker.of(
                        Move.of(Coordinate.of(-1, -1), Coordinate.of(-1, -1)), 0
                )
        );
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
    public String getFen() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int countOfEmptyCells = 0;
            for (int j = 0; j < 8; j++) {
                Cell cell = getCell(Coordinate.of(j, 8 - i - 1));
                if (cell.isEmpty()) countOfEmptyCells++;
                else {
                    if (countOfEmptyCells != 0) {
                        stringBuilder.append(countOfEmptyCells);
                        countOfEmptyCells = 0;
                    }
                    stringBuilder.append(cell);
                }
            }
            if (countOfEmptyCells != 0) stringBuilder.append(countOfEmptyCells);
            if (i != 7) stringBuilder.append("/");
        }
        stringBuilder.append(" ").append(turn == Turn.WHITE ? "w" : "b");
        stringBuilder.append(" ").append(isRoqueNotUsed[0] ? "K" : "");
        stringBuilder.append(isRoqueNotUsed[1] ? "Q" : "").append(isRoqueNotUsed[2] ? "k" : "");
        stringBuilder.append(isRoqueNotUsed[3] ? "q" : "").append(" ");
        stringBuilder.append(enPassant == null ? "-" : enPassant);
        stringBuilder.append(" ").append(draw50MovesRule).append(" ").append(moveNumber - 1);
        return stringBuilder.toString();
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