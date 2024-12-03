package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Plane");

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyPlane flappyPlane = new FlappyPlane();
        frame.add(flappyPlane);
        frame.pack();
        flappyPlane.requestFocus();
        frame.setVisible(true);
    }
}