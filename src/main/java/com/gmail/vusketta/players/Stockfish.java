package com.gmail.vusketta.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Stockfish {
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    private static final String PATH = ".\\src\\main\\engine\\stockfish-windows-2022-x86-64-avx2.exe";

    public void startEngine() {
        try {
            Process engineProcess = new ProcessBuilder(PATH).start();
            processReader = new BufferedReader(new InputStreamReader(
                    engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(
                    engineProcess.getOutputStream());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getOutput(int waitTime) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Thread.sleep(waitTime);
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    stringBuilder.append(text).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public String getBestMove(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime 1");
        int waitTime = 0;
        String[] output;
        do {
            waitTime += 100;
            output = getOutput(waitTime).split("bestmove ");
        } while (output.length == 1);
        System.out.println(waitTime);
        return output[1].split(" ")[0];
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException ignored) {
        }
    }
}