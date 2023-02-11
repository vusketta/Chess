package com.gmail.vusketta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardUtils {
    public static boolean inside(final Coordinate coordinate) {
        final int x = coordinate.x();
        final int y = coordinate.y();
        return 0 <= x && x < 8 && 0 <= y && y < 8;
    }

    public static boolean checkPieceIsNotNearKing(final Coordinate piece, final Coordinate king) {
        final int dx = Math.abs(king.x() - piece.x());
        final int dy = Math.abs(king.y() - piece.y());
        return !(dx == 1 && dy == 0 || dx == 0 && dy == 1 || dx == 1 && dy == 1);
    }

    public static List<Move> getPieceMoves(final Cell piece, final Coordinate coordinate) {
        return switch (piece) {
            case WHITE_PAWN -> getPawnMoves(true, coordinate);
            case BLACK_PAWN -> getPawnMoves(false, coordinate);
            case WHITE_ROOK, BLACK_ROOK -> getRookMoves(coordinate);
            case WHITE_KNIGHT, BLACK_KNIGHT -> getKnightMoves(coordinate);
            case WHITE_BISHOP, BLACK_BISHOP -> getBishopMoves(coordinate);
            case WHITE_QUEEN, BLACK_QUEEN -> getQueenMoves(coordinate);
            case WHITE_KING, BLACK_KING -> getKingMoves(coordinate);
            case EMPTY -> new ArrayList<>();
        };
    }

    public static List<Move> getPawnMoves(final boolean isWhite, final Coordinate coordinate) {
        final int x = coordinate.x();
        final int y = coordinate.y();
        return List.of(
                Move.of(coordinate, Coordinate.of(x, y + (isWhite ? 1 : -1))),
                Move.of(coordinate, Coordinate.of(x, y + (isWhite ? 1 : -1))),
                Move.of(coordinate, Coordinate.of(x - 1, y + (isWhite ? 1 : -1))),
                Move.of(coordinate, Coordinate.of(x + 1, y + (isWhite ? 2 : -2))),
                Move.of(coordinate, Coordinate.of(x - 1, isWhite ? 5 : 2)),
                Move.of(coordinate, Coordinate.of(x + 1, isWhite ? 5 : 2))
        );
    }

    public static List<Move> getRookMoves(final Coordinate coordinate) {
        final List<Move> moves = new ArrayList<>();
        final List<Coordinate> rooks = getRookAttacks(coordinate.x(), coordinate.y());
        for (Coordinate rook : rooks) moves.add(Move.of(coordinate, rook));
        return moves;
    }

    public static List<Move> getKnightMoves(final Coordinate coordinate) {
        final List<Move> moves = new ArrayList<>();
        final List<Coordinate> knights = getKnightAttacks(coordinate.x(), coordinate.y());
        for (Coordinate knight : knights) moves.add(Move.of(coordinate, knight));
        return moves;
    }

    public static List<Move> getBishopMoves(final Coordinate coordinate) {
        final List<Move> moves = new ArrayList<>();
        final List<Coordinate> bishops = getBishopAttacks(coordinate.x(), coordinate.y());
        for (Coordinate bishop : bishops) moves.add(Move.of(coordinate, bishop));
        return moves;
    }

    public static List<Move> getQueenMoves(final Coordinate coordinate) {
        final List<Move> moves = new ArrayList<>();
        moves.addAll(getBishopMoves(coordinate));
        moves.addAll(getRookMoves(coordinate));
        return moves;
    }

    public static List<Move> getKingMoves(final Coordinate coordinate) {
        final int x = coordinate.x();
        final int y = coordinate.y();
        return List.of(
                Move.of(coordinate, Coordinate.of(x + 1, y + 1)), Move.of(coordinate, Coordinate.of(x + 1, y)),
                Move.of(coordinate, Coordinate.of(x + 1, y - 1)), Move.of(coordinate, Coordinate.of(x, y - 1)),
                Move.of(coordinate, Coordinate.of(x, y + 1)), Move.of(coordinate, Coordinate.of(x - 1, y + 1)),
                Move.of(coordinate, Coordinate.of(x - 1, y)), Move.of(coordinate, Coordinate.of(x - 1, y - 1)),
                Move.of(coordinate, Coordinate.of(x + 2, y)), Move.of(coordinate, Coordinate.of(x - 2, y))
        );
    }

    public static List<Coordinate> getRookAttacks(final int x, final int y) {
        final List<Coordinate> rooks = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            rooks.add(Coordinate.of(x, i));
            rooks.add(Coordinate.of(i, y));
        }
        return rooks;
    }

    public static List<Coordinate> getKnightAttacks(final int x, final int y) {
        return List.of(
                Coordinate.of(x + 2, y + 1), Coordinate.of(x + 2, y - 1),
                Coordinate.of(x - 2, y + 1), Coordinate.of(x - 2, y - 1),
                Coordinate.of(x + 1, y + 2), Coordinate.of(x + 1, y - 2),
                Coordinate.of(x - 1, y + 2), Coordinate.of(x - 1, y - 2)
        );
    }

    public static List<Coordinate> getBishopAttacks(final int x, final int y) {
        final List<Coordinate> bishops = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            bishops.add(Coordinate.of(x + i, y + i));
            bishops.add(Coordinate.of(x + i, y - i));
            bishops.add(Coordinate.of(x - i, y + i));
            bishops.add(Coordinate.of(x - i, y - i));
        }
        return bishops;
    }

    public static Map<Character, Integer> getNotation() {
        return Map.of(
                'a', 1, 'b', 2, 'c', 3, 'd', 4,
                'e', 5, 'f', 6, 'g', 7, 'h', 8
        );
    }

    public static Map<Integer, Character> getReversedNotation() {
        return Map.of(
                1, 'a', 2, 'b', 3, 'c', 4, 'd', 5,
                'e', 6, 'f', 7, 'g', 8, 'h'
        );
    }
}
