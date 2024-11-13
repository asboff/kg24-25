import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageProcessingApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private JLabel imageLabel;

    public ImageProcessingApp() {
        super("Image Processing Application");

        // Настройка окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load Image");
        JButton thresholdButton = new JButton("Apply Threshold");
        JButton otsuButton = new JButton("Apply Otsu Threshold");
        JButton sharpenButton = new JButton("Apply Sharpen Filter");

        buttonPanel.add(loadButton);
        buttonPanel.add(thresholdButton);
        buttonPanel.add(otsuButton);
        buttonPanel.add(sharpenButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Отображение изображения
        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        // Обработчики событий для кнопок
        loadButton.addActionListener(e -> loadImage());
        thresholdButton.addActionListener(e -> applyFixedThreshold(128)); // Пример порога 128
        otsuButton.addActionListener(e -> applyOtsuThreshold());
        sharpenButton.addActionListener(e -> applySharpenFilter());

        setVisible(true);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedImage loadedImage = ImageIO.read(file);

                // Определяем максимальные размеры для масштабирования
                int maxWidth = getWidth() - 50;
                int maxHeight = getHeight() - 150;

                // Масштабируем изображение, если оно превышает размеры окна
                if (loadedImage.getWidth() > maxWidth || loadedImage.getHeight() > maxHeight) {
                    originalImage = resizeImage(loadedImage, maxWidth, maxHeight);
                } else {
                    originalImage = loadedImage;
                }

                processedImage = originalImage;
                imageLabel.setIcon(new ImageIcon(originalImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Рассчитываем соотношение сторон
        double widthRatio = (double) maxWidth / width;
        double heightRatio = (double) maxHeight / height;
        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        // Создаём новое изображение с нужными размерами
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    // Метод для фиксированной пороговой обработки
    private void applyFixedThreshold(int threshold) {
        processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int rgb = originalImage.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;
                int newColor = (gray >= threshold) ? 0xFFFFFF : 0x000000;
                processedImage.setRGB(x, y, newColor);
            }
        }
        imageLabel.setIcon(new ImageIcon(processedImage));
    }

    // Метод пороговой обработки по Оцу
    private void applyOtsuThreshold() {
        int[] histogram = new int[256];
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int gray = (originalImage.getRGB(x, y) >> 16) & 0xff;
                histogram[gray]++;
            }
        }

        // Вычисляем порог Оцу
        int total = originalImage.getWidth() * originalImage.getHeight();
        int sum = 0;
        for (int i = 0; i < 256; i++) sum += i * histogram[i];
        int sumB = 0, wB = 0, wF = 0, max = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histogram[t];
            if (wB == 0) continue;
            wF = total - wB;
            if (wF == 0) break;

            sumB += t * histogram[t];
            int mB = sumB / wB;
            int mF = (sum - sumB) / wF;

            int between = wB * wF * (mB - mF) * (mB - mF);
            if (between > max) {
                max = between;
                threshold = t;
            }
        }
        applyFixedThreshold(threshold);
    }

    // Метод увеличения резкости изображения
    private void applySharpenFilter() {
        float[] sharpenKernel = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };

        processedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int y = 1; y < originalImage.getHeight() - 1; y++) {
            for (int x = 1; x < originalImage.getWidth() - 1; x++) {
                int rgb = applyKernel(sharpenKernel, x, y);
                processedImage.setRGB(x, y, rgb);
            }
        }
        imageLabel.setIcon(new ImageIcon(processedImage));
    }

    // Применение ядра для фильтра
    private int applyKernel(float[] kernel, int x, int y) {
        int red = 0, green = 0, blue = 0;
        int k = 0;
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int rgb = originalImage.getRGB(x + i, y + j);
                red += ((rgb >> 16) & 0xff) * kernel[k];
                green += ((rgb >> 8) & 0xff) * kernel[k];
                blue += (rgb & 0xff) * kernel[k];
                k++;
            }
        }
        red = Math.min(Math.max(red, 0), 255);
        green = Math.min(Math.max(green, 0), 255);
        blue = Math.min(Math.max(blue, 0), 255);
        return (red << 16) | (green << 8) | blue;
    }

    public static void main(String[] args) {
        new ImageProcessingApp();
    }
}
