package FruityThrow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Importy potrzebne do obsługi dźwięku
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class FruityThrowDisplay {

    public FruityThrowDisplay() {
        JFrame frame = new JFrame("Fruity Throw");
        frame.setSize(800, 660);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        MainPanel mainPanel = new MainPanel();
        mainPanel.setLayout(null);
        frame.add(mainPanel);

        JMenuBar menuBar = new JMenuBar();

        JMenu aboutMenu = new JMenu("O autorach");
        JMenuItem authorsItem = new JMenuItem("Zobacz autorów");
        authorsItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Projekt Fruity Throw\nAutorzy: Julia Sołtysiak, Julia Stefaniak",
                "O autorach",
                JOptionPane.INFORMATION_MESSAGE));
        aboutMenu.add(authorsItem);

        JMenu secretMenu = new JMenu("Sekret");
        JMenuItem secretItem = new JMenuItem("Odsłoń sekret");
        secretItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Kochamy programować!! (L)",
                "Sekret",
                JOptionPane.PLAIN_MESSAGE));
        secretMenu.add(secretItem);

        JMenu soundMenu = new JMenu("Dźwięk");
        JMenuItem playSoundItem = new JMenuItem("Odtwórz dźwięk");
        playSoundItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("JuliasFruitThrow2.wav");
            }
        });
        soundMenu.add(playSoundItem);

        menuBar.add(aboutMenu);
        menuBar.add(secretMenu);
        menuBar.add(soundMenu);
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    public void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                System.out.println("Plik dźwiękowy nie został znaleziony: " + soundFilePath);
                JOptionPane.showMessageDialog(null, "Nie znaleziono pliku dźwiękowego: ", "Błąd audio", JOptionPane.ERROR_MESSAGE);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas odtwarzania dźwięku.", "Błąd audio", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        new FruityThrowDisplay();
    }
}

/******JULIA SOŁTYSIAK******/