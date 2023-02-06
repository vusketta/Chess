package com.gmail.vusketta;

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
            String input = in.next();
            int fromX = Integer.parseInt(String.valueOf(input.charAt(0)));
            int fromY = Integer.parseInt(String.valueOf(input.charAt(1)));
            int toX = Integer.parseInt(String.valueOf(input.charAt(3)));
            int toY = Integer.parseInt(String.valueOf(input.charAt(4)));
            from = Coordinate.of(fromX - 1, fromY - 1);
            to = Coordinate.of(toX - 1, toY - 1);
            if (position.isValid(Move.of(from, to))) break;
            System.out.println("Input move is incorrect");
            System.out.println("Try again");
        }

        return new Move(from, to);
    }
}
