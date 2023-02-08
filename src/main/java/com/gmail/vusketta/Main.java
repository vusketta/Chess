package com.gmail.vusketta;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Game RANDOM_VS_RANDOM = new TwoPlayerGame(
                new ChessBoard(),
                new RandomPlayer(),
                new RandomPlayer()
        );
        final Game HUMAN_VS_RANDOM = new TwoPlayerGame(
                new ChessBoard(),
                new HumanPlayer(new Scanner(System.in)),
                new RandomPlayer()
        );
        final Game HUMAN_VS_HUMAN = new TwoPlayerGame(
                new ChessBoard(),
                new HumanPlayer(new Scanner(System.in)),
                new HumanPlayer(new Scanner(System.in))
        );
        final int result = HUMAN_VS_RANDOM.play(true);
        switch (result) {
            case 1 -> System.out.println("First player won");
            case 2 -> System.out.println("Second player won");
            case 0 -> System.out.println("Draw");
            default -> throw new AssertionError("Unknown result " + result);
        }
    }
}