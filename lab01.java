import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JSlider[] rgbSliders = new JSlider[3];
    private JSlider[] cmykSliders = new JSlider[4];
    private JSlider[] hsvSliders = new JSlider[3];
    private JTextField[] rgbFields = new JTextField[3];
    private JTextField[] cmykFields = new JTextField[4];
    private JTextField[] hsvFields = new JTextField[3];
    private JPanel colorPanel;
    private JButton colorPickerButton;  // Кнопка для выбора цвета
    private boolean isUpdating = false;  // Флаг для предотвращения рекурсии

    public Main() {
        setTitle("Color Picker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Панель выбора цвета
        colorPanel = new JPanel();
        colorPanel.setBackground(Color.WHITE);
        add(colorPanel, BorderLayout.CENTER);

        // Панель с ползунками и полями ввода
        JPanel slidersPanel = new JPanel(new GridLayout(3, 1));
        add(slidersPanel, BorderLayout.EAST);

        // Панели для RGB, CMYK, HSV
        JPanel rgbPanel = createColorModelPanel("RGB", rgbSliders, rgbFields, 255);
        JPanel cmykPanel = createColorModelPanel("CMYK", cmykSliders, cmykFields, 100);
        JPanel hsvPanel = createColorModelPanel("HSV", hsvSliders, hsvFields, 360);

        slidersPanel.add(rgbPanel);
        slidersPanel.add(cmykPanel);
        slidersPanel.add(hsvPanel);

        // Добавление кнопки выбора цвета
        colorPickerButton = new JButton("Choose Color");
        colorPickerButton.addActionListener(e -> chooseColor());
        add(colorPickerButton, BorderLayout.SOUTH);

        // Логика изменения цвета через ползунки
        setupColorSliders();

        setVisible(true);
    }

    private JPanel createColorModelPanel(String title, JSlider[] sliders, JTextField[] fields, int max) {
        JPanel panel = new JPanel(new GridLayout(sliders.length, 2));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        for (int i = 0; i < sliders.length; i++) {
            sliders[i] = new JSlider(0, max);
            fields[i] = new JTextField(4);
            panel.add(sliders[i]);
            panel.add(fields[i]);
        }
        return panel;
    }

    private void setupColorSliders() {
        // Добавление слушателей изменений для ползунков и полей для RGB
        for (int i = 0; i < rgbSliders.length; i++) {
            final int index = i;
            rgbSliders[i].addChangeListener(e -> updateColorFromRGB());
            rgbFields[i].addActionListener(e -> {
                int value = Integer.parseInt(rgbFields[index].getText());
                rgbSliders[index].setValue(value);
            });
        }
        // Аналогичные действия для CMYK и HSV
        for (int i = 0; i < cmykSliders.length; i++) {
            final int index = i;
            cmykSliders[i].addChangeListener(e -> updateColorFromCMYK());
            cmykFields[i].addActionListener(e -> {
                int value = Integer.parseInt(cmykFields[index].getText());
                cmykSliders[index].setValue(value);
            });
        }
        for (int i = 0; i < hsvSliders.length; i++) {
            final int index = i;
            hsvSliders[i].addChangeListener(e -> updateColorFromHSV());
            hsvFields[i].addActionListener(e -> {
                int value = Integer.parseInt(hsvFields[index].getText());
                hsvSliders[index].setValue(value);
            });
        }
    }

    private void chooseColor() {
        Color chosenColor = JColorChooser.showDialog(this, "Choose a Color", colorPanel.getBackground());
        if (chosenColor != null) {
            updateColorFromChooser(chosenColor);
        }
    }

    private void updateColorFromChooser(Color color) {
        if (isUpdating) return;  // Предотвращение рекурсии
        isUpdating = true;

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        Color newColor = new Color(r, g, b);
        colorPanel.setBackground(newColor);

        // Обновляем поля ввода RGB
        rgbSliders[0].setValue(r);
        rgbSliders[1].setValue(g);
        rgbSliders[2].setValue(b);
        rgbFields[0].setText(String.valueOf(r));
        rgbFields[1].setText(String.valueOf(g));
        rgbFields[2].setText(String.valueOf(b));

        // Обновляем остальные модели
        updateCMYKFromRGB(r, g, b);
        updateHSVFromRGB(r, g, b);

        isUpdating = false;
    }

    private void updateColorFromRGB() {
        if (isUpdating) return;  // Предотвращение рекурсии
        isUpdating = true;

        int r = rgbSliders[0].getValue();
        int g = rgbSliders[1].getValue();
        int b = rgbSliders[2].getValue();
        Color color = new Color(r, g, b);
        colorPanel.setBackground(color);

        // Обновляем поля ввода RGB
        rgbFields[0].setText(String.valueOf(r));
        rgbFields[1].setText(String.valueOf(g));
        rgbFields[2].setText(String.valueOf(b));

        // Обновляем остальные модели
        updateCMYKFromRGB(r, g, b);
        updateHSVFromRGB(r, g, b);

        isUpdating = false;
    }

    private void updateCMYKFromRGB(int r, int g, int b) {
        // Алгоритм преобразования RGB в CMYK
        float rNorm = r / 255.0f;
        float gNorm = g / 255.0f;
        float bNorm = b / 255.0f;
        float k = 1 - Math.max(rNorm, Math.max(gNorm, bNorm));
        float c = (1 - rNorm - k) / (1 - k);
        float m = (1 - gNorm - k) / (1 - k);
        float y = (1 - bNorm - k) / (1 - k);

        cmykSliders[0].setValue((int) (c * 100));
        cmykSliders[1].setValue((int) (m * 100));
        cmykSliders[2].setValue((int) (y * 100));
        cmykSliders[3].setValue((int) (k * 100));

        cmykFields[0].setText(String.valueOf((int) (c * 100)));
        cmykFields[1].setText(String.valueOf((int) (m * 100)));
        cmykFields[2].setText(String.valueOf((int) (y * 100)));
        cmykFields[3].setText(String.valueOf((int) (k * 100)));
    }

    private void updateHSVFromRGB(int r, int g, int b) {
        // Алгоритм преобразования RGB в HSV
        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);

        hsvSliders[0].setValue((int) (hsv[0] * 360));
        hsvSliders[1].setValue((int) (hsv[1] * 100));
        hsvSliders[2].setValue((int) (hsv[2] * 100));

        hsvFields[0].setText(String.valueOf((int) (hsv[0] * 360)));
        hsvFields[1].setText(String.valueOf((int) (hsv[1] * 100)));
        hsvFields[2].setText(String.valueOf((int) (hsv[2] * 100)));
    }

    private void updateColorFromCMYK() {
        if (isUpdating) return;  // Предотвращение рекурсии
        isUpdating = true;

        // Пример преобразования CMYK в RGB
        float c = cmykSliders[0].getValue() / 100f;
        float m = cmykSliders[1].getValue() / 100f;
        float y = cmykSliders[2].getValue() / 100f;
        float k = cmykSliders[3].getValue() / 100f;

        int r = (int) (255 * (1 - c) * (1 - k));
        int g = (int) (255 * (1 - m) * (1 - k));
        int b = (int) (255 * (1 - y) * (1 - k));

        // Обновляем RGB ползунки и поля
        rgbSliders[0].setValue(r);
        rgbSliders[1].setValue(g);
        rgbSliders[2].setValue(b);

        // Обновляем цвет панели
        Color color = new Color(r, g, b);
        colorPanel.setBackground(color);

        // Обновляем HSV после RGB
        updateHSVFromRGB(r, g, b);

        isUpdating = false;
    }

    private void updateColorFromHSV() {
        if (isUpdating) return;  // Предотвращение рекурсии
        isUpdating = true;

        // Пример преобразования HSV в RGB
        float h = hsvSliders[0].getValue() / 360f;
        float s = hsvSliders[1].getValue() / 100f;
        float v = hsvSliders[2].getValue() / 100f;

        int rgb = Color.HSBtoRGB(h, s, v);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // Обновляем RGB ползунки и поля
        rgbSliders[0].setValue(r);
        rgbSliders[1].setValue(g);
        rgbSliders[2].setValue(b);

        // Обновляем цвет панели
        Color color = new Color(r, g, b);
        colorPanel.setBackground(color);

        // Обновляем CMYK после RGB
        updateCMYKFromRGB(r, g, b);

        isUpdating = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
