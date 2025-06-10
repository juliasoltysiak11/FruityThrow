package FruityThrow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class MainPanel extends JPanel {
    private Image fruitImage;
    private Image binImage;
    private Image cloudImage;
    private Color skyColor = new Color(135, 206, 250);
    private final String[] fruitOptions = { "Jabłko", "Banan", "Pomarańcza", "Marakuja" };
    private String selectedFruit = "apple.png";
    private Random random = new Random();
    private JSlider velocitySlider;
    private JSlider gravitySlider;

    private static final int initialFruitX = 34;
    private static final int initialFruitY = 437;
    private static final int fruitWidth = 34;
    private static final int fruitHeight = 40;
    private static final int binWidth = 85;
    private static final int binHeight = 95;
    private int binX = 450 + random.nextInt(200);
    private static final int binY = 442;
    private static final int cloudWidth = 400;
    private static final int cloudHeight = 150;

    private double currentFruitX = initialFruitX;
    private double currentFruitY = initialFruitY;

    private Timer timer;
    private double simulationSpeed = 1.0;

    private FruitAnimation fruitAnimation;

    private double launchAngleDegrees = 45.0;
    private Point dragStartPoint;
    private Point currentMousePoint;
    private boolean isDraggingAngle = false;

    private final int cloudCount = 3;
    private int[] cloudXPositions = new int[cloudCount];
    private int[] cloudYPositions = new int[cloudCount];
    private int[] cloudSpeeds = new int[cloudCount];


    public MainPanel() {
        setLayout(null);

        JComboBox<String> fruitSelector = new JComboBox<>(fruitOptions);
        fruitSelector.setBounds(27, 491, 101, 24);
        fruitSelector.addActionListener(e -> {
            String choice = (String) fruitSelector.getSelectedItem();
            switch (choice) {
                case "Banan":
                    selectedFruit = "banana.png";
                    break;
                case "Pomarańcza":
                    selectedFruit = "orange.png";
                    break;
                case "Jabłko":
                    selectedFruit = "apple.png";
                    break;
                case "Marakuja":
                    selectedFruit = "marakuja.png";
                    break;
            }
            loadFruitImage();
            repaint();
        });
        add(fruitSelector);

        JButton randomBackgroundButton = new JButton("Losuj tło");
        randomBackgroundButton.setBounds(27, 523, 101, 32);
        randomBackgroundButton.addActionListener(e -> {
            skyColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            repaint();
        });
        add(randomBackgroundButton);

        JButton startButton = new JButton("Start/Restart");
        startButton.setBounds(27, 563, 101, 32);
        startButton.addActionListener(e -> startSimulation());
        add(startButton);

        add(createSliderRow(" Prędkość (m/s):", 0, 100, 50, 169, 529));
        add(createSliderRow(" Grawitacja (m/s²):", 0, 30, 10, 169, 567));

        JPanel speedPanel = new JPanel();
        speedPanel.setBounds(480, 563, 270, 32);
        speedPanel.setBackground(new Color(50, 50, 50));

        JRadioButton speed025 = new JRadioButton("0.25x");
        speed025.addActionListener(e -> simulationSpeed = 0.25);
        JRadioButton speed05 = new JRadioButton("0.5x");
        speed05.addActionListener(e -> simulationSpeed = 0.5);
        JRadioButton speed1 = new JRadioButton("1x", true);
        speed1.addActionListener(e -> simulationSpeed = 1.0);
        JRadioButton speed2 = new JRadioButton("2x");
        speed2.addActionListener(e -> simulationSpeed = 2.0);
        JRadioButton speed4 = new JRadioButton("4x");
        speed4.addActionListener(e -> simulationSpeed = 4.0);

        ButtonGroup speedGroup = new ButtonGroup();
        speedGroup.add(speed025);
        speedGroup.add(speed05);
        speedGroup.add(speed1);
        speedGroup.add(speed2);
        speedGroup.add(speed4);

        JRadioButton[] radioButtons = new JRadioButton[] { speed025, speed05, speed1, speed2, speed4 };
        for (JRadioButton rb : radioButtons) {
            rb.setForeground(Color.WHITE);
            rb.setBackground(new Color(50, 50, 50));
            speedPanel.add(rb);
        }
        add(speedPanel);

        loadFruitImage();
        loadBinImage();
        
        loadCloudImage();
        initializeClouds();
        startCloudAnimation();

        MouseAdapter mouseDealer = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (timer != null && timer.isRunning()) {
                    return;
                }

                Rectangle fruitBounds = new Rectangle(initialFruitX, initialFruitY, fruitWidth, fruitHeight);

                if (fruitBounds.contains(e.getPoint())) {
                    isDraggingAngle = true; 
                    dragStartPoint = new Point(initialFruitX + fruitWidth / 2, initialFruitY + fruitHeight / 2);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            	if (isDraggingAngle) { 

                launchAngleDegrees = Math.toDegrees(
                    Math.atan2(dragStartPoint.y - e.getY(), e.getX() - dragStartPoint.x)
                );
                launchAngleDegrees = Math.max(0, Math.min(90, launchAngleDegrees));

                currentMousePoint = e.getPoint();
                repaint();
            }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDraggingAngle) {
                    isDraggingAngle = false;
                    repaint();
                }
            }
        };

        addMouseListener(mouseDealer);
        addMouseMotionListener(mouseDealer);
    }

    private JPanel createSliderRow(String labelText, int min, int max, int initial, int x, int y) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(x, y, 260, 30);
        panel.setBackground(new Color(50, 50, 50));

        JLabel label = new JLabel(labelText);
        label.setBounds(0, 5, 110, 20);
        label.setForeground(Color.WHITE);
        panel.add(label);

        JSlider slider = new JSlider(min, max, initial);
        slider.setBounds(105, 0, 100, 30);
        panel.add(slider);

        JTextField valueField = new JTextField(String.valueOf(initial));
        valueField.setBounds(210, 5, 45, 20);
        valueField.setEditable(false);
        panel.add(valueField);

        slider.addChangeListener(e -> valueField.setText(String.valueOf(slider.getValue())));

        if (labelText.contains("Prędkość")) {
            velocitySlider = slider;
        } else if (labelText.contains("Grawitacja")) {
            gravitySlider = slider;
        }
        return panel;
    }

    private void loadCloudImage() {
        try {
            cloudImage = ImageIO.read(new File("cloud.png"));
        } catch (IOException e) {
            System.err.println("Nie da się wczytać obrazka.");
            e.printStackTrace();
        }
    }

    private void loadFruitImage() {
        try {
            fruitImage = ImageIO.read(new File(selectedFruit));
        } catch (IOException e) {
            System.err.println("Nie da się wczytać obrazka.");
            e.printStackTrace();
        }
    }

    private void loadBinImage() {
        try {
            binImage = ImageIO.read(new File("kosz_bio.png"));
        } catch (IOException e) {
            System.err.println("Nie da się wczytać obrazka.");
            e.printStackTrace();
        }
    }

    private void initializeClouds() {
        cloudYPositions[0] = 50;
        cloudSpeeds[0] = 1;
        cloudYPositions[1] = 100;
        cloudSpeeds[1] = 2;
        cloudYPositions[2] = 150;
        cloudSpeeds[2] = 1;

        int widthPanel = Math.max(getWidth(), 800); 
        for (int ii = 0; ii < cloudCount; ii++) {
            cloudXPositions[ii] = random.nextInt(widthPanel);
        }
    }

    private void startCloudAnimation() {
        Timer cloudTimer = new Timer(20, e -> {
            int panelWidth = getWidth();
            int currentCloudWidth = cloudImage.getWidth(this);

            for (int i = 0; i < cloudCount; i++) {
                cloudXPositions[i] += cloudSpeeds[i];
                if (cloudXPositions[i] > panelWidth) {
                    cloudXPositions[i] = -currentCloudWidth;
                }
            }
            repaint();
        });
        cloudTimer.start();
    }

    private void startSimulation() {
        double angleDegree = launchAngleDegrees;
        double velocity = velocitySlider.getValue();
        double gravity = gravitySlider.getValue();

        currentFruitX = initialFruitX;
        currentFruitY = initialFruitY;

        fruitAnimation = new FruitAnimation(angleDegree, velocity, gravity, currentFruitX, currentFruitY);

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(20, e -> {
            fruitAnimation.updateTime(0.05, simulationSpeed);
            double[] position = fruitAnimation.getPosition();
            currentFruitX = position[0];
            currentFruitY = position[1];
            repaint();

            if (fruitAnimation.isLanded(initialFruitY + fruitHeight /3.0)) {
                timer.stop();

                double fruitCenterX = currentFruitX + (fruitWidth / 2.0);
                boolean hit = fruitCenterX >= binX + binWidth / 4.0 && fruitCenterX <= (binX + 3.0 * binWidth / 4.0);
                
                showEndGameDialog(hit);
            }
        });
        timer.start();
    }

    private void showEndGameDialog(boolean wasHit) {
        String message;

        Color successColor = new Color(220, 255, 220);
        Color failureColor = new Color(255, 220, 220);

        if (wasHit) {
            message = "Celny rzut! Gratulacje!\nChcesz zagrać jeszcze raz?";
            UIManager.put("OptionPane.background", successColor);
            UIManager.put("Panel.background", successColor);
            int maxBin = getWidth() - binWidth - 300;
            if (maxBin <= 0) {
            	maxBin = 200;
            }
            binX = 300 + random.nextInt(maxBin);
        } else {
            message = "Pudło! \nChcesz spróbować ponownie?";
            UIManager.put("OptionPane.background", failureColor);
            UIManager.put("Panel.background", failureColor);
        }

        Object[] options = {"Zagraj ponownie", "Zakończ"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Koniec rundy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        } else {
            currentFruitX = initialFruitX;
            currentFruitY = initialFruitY;
            repaint();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(skyColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (cloudImage != null) {
            for (int ii = 0; ii < cloudCount; ii++) {
                g.drawImage(cloudImage, cloudXPositions[ii], cloudYPositions[ii], cloudWidth, cloudHeight, this);
            }
        }

        g.setColor(new Color(139, 69, 19));
        g.fillRect(0, 480, getWidth(), getHeight() - 480);
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 470, getWidth(), 10);

        int drawX;
        int drawY;
        if (timer != null && timer.isRunning()) {
            drawX = (int) currentFruitX;
            drawY = (int) currentFruitY;
        } else {
            drawX = initialFruitX;
            drawY = initialFruitY;
            this.currentFruitX = initialFruitX;
            this.currentFruitY = initialFruitY;
        }

        if (fruitImage != null) {
            g.drawImage(fruitImage, drawX, drawY, fruitWidth, fruitHeight, this);
        }
        if (binImage != null) {
            g.drawImage(binImage, binX, binY, binWidth, binHeight, this);
        }

        if (isDraggingAngle && dragStartPoint != null && currentMousePoint != null) {
            g.setColor(Color.RED);
            g.drawLine(dragStartPoint.x, dragStartPoint.y, currentMousePoint.x, currentMousePoint.y);
            String angleText = String.format("Kąt: %.1f°", launchAngleDegrees);
            g.setColor(Color.BLACK);
            g.drawString(angleText, dragStartPoint.x + 10, dragStartPoint.y - 20);
        } else if (timer == null || !timer.isRunning()) {
            String angleText = String.format("Kąt: %.1f° (Przeciągnij owoc, aby zmienić kąt)", launchAngleDegrees);
            g.setColor(Color.BLACK);
            g.drawString(angleText, initialFruitX + fruitWidth + 5, initialFruitY + fruitHeight / 2);
        }
    }
}

/******JULIA STEFANIAK, JULIA SOŁTYSIAK******/