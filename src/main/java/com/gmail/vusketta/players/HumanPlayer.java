package com.gmail.vusketta.players;

import com.gmail.vusketta.*;
import com.gmail.vusketta.board.BoardUtils;

import java.util.Map;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    @Override
    public Move makeMove(Position position) {
        System.out.println();
        System.out.println("Current position");
        System.out.println(position);
        System.out.println("Enter your move for " + position.getTurn());

        Coordinate from, to;

        while (true) {
            Map<Character, Integer> notation = BoardUtils.getNotation();
            String in1 = in.next();
            String in2 = in.next();
            int fromX = notation.get(in1.charAt(0)) - 1;
            int fromY = Integer.parseInt(String.valueOf(in1.charAt(1)));
            int toX = notation.get(in2.charAt(0)) - 1;
            int toY = Integer.parseInt(String.valueOf(in2.charAt(1)));
            from = Coordinate.of(fromX, fromY - 1);
            to = Coordinate.of(toX, toY - 1);
            if (position.isValid(Move.of(from, to))) break;
            System.out.println("Некорректный ход");
            System.out.println("Попробуйте ещё раз");
        }

        return new Move(from, to);
    }
}