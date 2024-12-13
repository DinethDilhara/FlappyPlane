package org.example;

import javazoom.jl.player.Player;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

public class FlappyPlane extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 800;
    int boardHeight = 640;

    Image backgroundImage;
    Image planeImage;
    Image upperTowerImage;
    Image bottomTowerImage;

    int planeX = boardWidth/8;
    int planeY = boardWidth/2;
    int planeWidth = 68 ;
    int planeHeight = 48 ;

    class Plane {
        int x = planeX;
        int y = planeY;
        int width = planeWidth;
        int height = planeHeight;
        Image img;

        Plane(Image img) {
            this.img = img;
        }
    }

    int towerX = boardWidth;
    int towerY = 0;
    int towerWidth = 80;
    int towerHeight = 500;

    class Tower {
        int x = towerX;
        int y = towerY;
        int width = towerWidth;
        int height = towerHeight;
        Image img;
        boolean passed = false;

        Tower(Image img) {
            this.img = img;
        }
    }

    Plane plane;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Tower> towers;

    Timer gameLoop;
    Timer placeTowerTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyPlane() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/org/example/flappyPlaneBackGroundImage.jpg"))).getImage();
        planeImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/org/example/flappyPlane.png"))).getImage();
        upperTowerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/org/example/upperTowerImage.png"))).getImage();
        bottomTowerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/org/example/bottomTowerImage.png"))).getImage();


        plane = new Plane(planeImage);
        towers = new ArrayList<Tower>();

        placeTowerTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placeTowerTimer.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    void placePipes() {

        int randomPipeY = (int) (towerY - (double) towerHeight /4 - Math.random()*((double) towerHeight /2));
        int openingSpace = boardHeight/4;

        Tower topTower = new Tower(upperTowerImage);
        topTower.y = randomPipeY;
        towers.add(topTower);

        Tower bottomTower = new Tower(bottomTowerImage);
        bottomTower.y = topTower.y  + towerHeight + openingSpace;
        towers.add(bottomTower);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, this.boardWidth, this.boardHeight, null);

        g.drawImage(planeImage, plane.x, plane.y, plane.width, plane.height, null);

        for (int i = 0; i < towers.size(); i++) {
            Tower tower = towers.get(i);
            g.drawImage(tower.img, tower.x, tower.y, tower.width, tower.height, null);
        }

        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        velocityY += gravity;
        plane.y += velocityY;
        plane.y = Math.max(plane.y, 0);

        for (Tower tower : towers) {
            tower.x += velocityX;

            if (!tower.passed && plane.x > tower.x + tower.width) {
                score += 0.5;
                tower.passed = true;
            }

            if (collision(plane, tower)) {
                gameOver = true;
            }
        }

        if (plane.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Plane a, Tower b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    private void playGameOverSound() {
        new Thread(() -> {
            try (InputStream soundStream = getClass().getResourceAsStream("/org/example/gameoversound.mp3")) {
                if (soundStream == null) throw new IOException("Sound file not found!");

                Player player = new Player(soundStream);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            if (gameLoop.isRunning()) {
                playGameOverSound();
                showGameOverAlert();
            }
            placeTowerTimer.stop();
            gameLoop.stop();
        }
    }

    private void showGameOverAlert() {
        int option = JOptionPane.showOptionDialog(
                this,
                "Game Over! Your Score: " + (int) score,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Try Again", "Exit"},
                "Try Again"
        );

        if (option == JOptionPane.YES_OPTION) {
            System.out.println("Try Again");
        } else {
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                plane.y = planeY;
                velocityY = 0;
                towers.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placeTowerTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
