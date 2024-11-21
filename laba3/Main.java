import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private Panel Panel = new Panel();
    private JTextField scaleField;

    public Main() {
        setTitle("Растровые Алгоритмы");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(10, 10));


        JPanel algorithmPanel = new JPanel();
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
        algorithmPanel.setBorder(BorderFactory.createTitledBorder("Алгоритм"));

        JRadioButton stepByStepButton = new JRadioButton("Пошаговый");
        JRadioButton ddaButton = new JRadioButton("ЦДА");
        JRadioButton bresenhamLineButton = new JRadioButton("Брезенхем (отрезок)");
        JRadioButton bresenhamCircleButton = new JRadioButton("Брезенхем (окружность)");

        ButtonGroup algorithmGroup = new ButtonGroup();
        algorithmGroup.add(stepByStepButton);
        algorithmGroup.add(ddaButton);
        algorithmGroup.add(bresenhamLineButton);
        algorithmGroup.add(bresenhamCircleButton);

        algorithmPanel.add(stepByStepButton);
        algorithmPanel.add(ddaButton);
        algorithmPanel.add(bresenhamLineButton);
        algorithmPanel.add(bresenhamCircleButton);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField x1Field = createFixedSizeTextField("0");
        JTextField y1Field = createFixedSizeTextField("0");
        JTextField x2Field = createFixedSizeTextField("0");
        JTextField y2Field = createFixedSizeTextField("0");
        JTextField radiusField = createFixedSizeTextField("0");

        JLabel x1Label = createFixedSizeLabel("X1:");
        JLabel y1Label = createFixedSizeLabel("Y1:");
        JLabel x2Label = createFixedSizeLabel("X2:");
        JLabel y2Label = createFixedSizeLabel("Y2:");
        JLabel radiusLabel = createFixedSizeLabel("Радиус:");

        JLabel scaleLabel = new JLabel("Масштаб:");
        scaleField = createFixedSizeTextField("30");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(x1Label, gbc);
        gbc.gridx = 1;
        inputPanel.add(x1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(y1Label, gbc);
        gbc.gridx = 1;
        inputPanel.add(y1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(x2Label, gbc);
        gbc.gridx = 1;
        inputPanel.add(x2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(y2Label, gbc);
        gbc.gridx = 1;
        inputPanel.add(y2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(radiusLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(radiusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(scaleLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(scaleField, gbc);

        JButton drawButton = new JButton("Нарисовать");
        JButton applyScaleButton = new JButton("Применить масштаб");

        ActionListener algorithmSelectionListener = e -> {
            boolean isCircle = bresenhamCircleButton.isSelected();

            radiusField.setVisible(isCircle);
            radiusLabel.setVisible(isCircle);
            x2Field.setVisible(!isCircle);
            y2Field.setVisible(!isCircle);
            x2Label.setVisible(!isCircle);
            y2Label.setVisible(!isCircle);

            inputPanel.revalidate();
            inputPanel.repaint();
        };


        stepByStepButton.addActionListener(algorithmSelectionListener);
        ddaButton.addActionListener(algorithmSelectionListener);
        bresenhamLineButton.addActionListener(algorithmSelectionListener);
        bresenhamCircleButton.addActionListener(algorithmSelectionListener);

        bresenhamLineButton.setSelected(true);
        algorithmSelectionListener.actionPerformed(null);

        controlPanel.add(algorithmPanel, BorderLayout.NORTH);
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(drawButton);
        buttonPanel.add(applyScaleButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.WEST);
        add(Panel, BorderLayout.CENTER);

        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = parseInput(x1Field.getText());
                int y1 = parseInput(y1Field.getText());
                int x2 = parseInput(x2Field.getText());
                int y2 = parseInput(y2Field.getText());
                int radius = parseInput(radiusField.getText());

                int scale = parseScaleInput(scaleField.getText());
                String selectedAlgorithm = "";
                if (stepByStepButton.isSelected()) {
                    selectedAlgorithm = "Пошаговый";
                } else if (ddaButton.isSelected()) {
                    selectedAlgorithm = "ЦДА";
                } else if (bresenhamLineButton.isSelected()) {
                    selectedAlgorithm = "Брезенхем (отрезок)";
                } else if (bresenhamCircleButton.isSelected()) {
                    selectedAlgorithm = "Брезенхем (окружность)";
                }

                Panel.draw(selectedAlgorithm, x1, y1, x2, y2, radius, scale);
            }
        });

        applyScaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int scale = parseScaleInput(scaleField.getText());
                Panel.setScale(scale);
            }
        });
    }

    private int parseInput(String text) {
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseScaleInput(String text) {
        if (text.isEmpty()) {
            return 30;
        }
        try {
            int scale = Integer.parseInt(text);
            if (scale < 1) {
                return 30;
            }
            return scale;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    private JTextField createFixedSizeTextField(String initialValue) {
        JTextField textField = new JTextField(initialValue, 10);
        textField.setPreferredSize(new Dimension(100, 30));
        textField.setMinimumSize(new Dimension(100, 30));
        textField.setMaximumSize(new Dimension(100, 30));
        return textField;
    }

    private JLabel createFixedSizeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(50, 30));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}