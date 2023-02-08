package com.gmail.vusketta;

import java.util.Map;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final Map<Character, Integer> NOTATION = Map.of(
            'a', 1, 'b', 2, 'c', 3, 'd', 4,
            'e', 5, 'f', 6, 'g', 7, 'h', 8
    );
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
            String in1 = in.next();
            String in2 = in.next();
            int fromX = NOTATION.get(in1.charAt(0));
            int fromY = Integer.parseInt(String.valueOf(in1.charAt(1)));
            int toX = NOTATION.get(in2.charAt(0));
            int toY = Integer.parseInt(String.valueOf(in2.charAt(1)));
            from = Coordinate.of(fromX, fromY - 1);
            to = Coordinate.of(toX, toY - 1);
            if (position.isValid(Move.of(from, to))) break;
            System.out.println("Input move is incorrect");
            System.out.println("Try again");
        }

        return new Move(from, to);
    }
}
