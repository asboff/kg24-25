import javax.swing.*;
import java.awt.*;

class Panel extends JPanel {
    private int scale = 30;
    private String currentAlgorithm;
    private int x1, y1, x2, y2, radius;

    public Panel() {
        setBackground(Color.WHITE);
    }

    public void setScale(int scale) {
        this.scale = scale;
        repaint();
    }

    public void draw(String algorithm, int x1, int y1, int x2, int y2, int radius, int scale) {
        this.currentAlgorithm = algorithm;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.radius = radius;
        this.scale = scale;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCoordinatePlane(g);

        if (currentAlgorithm == null) {
            return;
        }

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        switch (currentAlgorithm) {
            case "Пошаговый":
                drawStepByStep(g, x1, y1, x2, y2, centerX, centerY);
                break;
            case "ЦДА":
                drawDDA(g, x1, y1, x2, y2, centerX, centerY);
                break;
            case "Брезенхем (отрезок)":
                drawBresenhamLine(g, x1, y1, x2, y2, centerX, centerY);
                break;
            case "Брезенхем (окружность)":
                drawCircleBresenham(g, x1, y1, radius, centerX, centerY);
                break;
        }
    }

    private void drawCoordinatePlane(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        for (int x = centerX % scale; x < width; x += scale) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = centerY % scale; y < height; y += scale) {
            g.drawLine(0, y, width, y);
        }

        g.setColor(Color.BLACK);
        g.drawLine(centerX, 0, centerX, height);
        g.drawLine(0, centerY, width, centerY);

        g.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, 10);
        g.setFont(font);

        for (int x = centerX; x < width; x += scale) {
            g.drawLine(x, centerY - 5, x, centerY + 5);
            if ((x - centerX) / scale != 0) {
                g.drawString(String.valueOf((x - centerX) / scale), x - 5, centerY + 15);
            }
        }
        for (int x = centerX - scale; x > 0; x -= scale) {
            g.drawLine(x, centerY - 5, x, centerY + 5);
            g.drawString(String.valueOf((x - centerX) / scale), x - 10, centerY + 15);
        }

        for (int y = centerY; y < height; y += scale) {
            g.drawLine(centerX - 5, y, centerX + 5, y);
            if ((centerY - y) / scale != 0) {
                g.drawString(String.valueOf(-(y - centerY) / scale), centerX + 10, y + 5);
            }
        }
        for (int y = centerY - scale; y > 0; y -= scale) {
            g.drawLine(centerX - 5, y, centerX + 5, y);
            g.drawString(String.valueOf((centerY - y) / scale), centerX + 10, y + 5);
        }

        g.drawString("(0, 0)", centerX + 5, centerY + 15);
    }

    private void drawPixel(Graphics g, int x, int y, int centerX, int centerY) {
        int screenX = centerX + x * scale;
        int screenY = centerY - y * scale - scale;
        g.fillRect(screenX, screenY, scale, scale);
    }

    private void drawStepByStep(Graphics g, int x1, int y1, int x2, int y2, int centerX, int centerY) {
        g.setColor(Color.BLACK);

        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;

        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            drawPixel(g, Math.round(x), Math.round(y), centerX, centerY);
            x += xIncrement;
            y += yIncrement;
        }
    }

    private void drawDDA(Graphics g, int x0, int y0, int x1, int y1, int centerX, int centerY) {
        g.setColor(Color.BLACK);

        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;

        float x = x0;
        float y = y0;

        drawPixel(g, Math.round(x), Math.round(y), centerX, centerY);

        for (int i = 1; i <= steps; i++) {
            x += xIncrement;
            y += yIncrement;
            drawPixel(g, Math.round(x), Math.round(y), centerX, centerY);
        }
    }



    private void drawBresenhamLine(Graphics g, int x0, int y0, int x1, int y1, int centerX, int centerY) {
        g.setColor(Color.BLACK);

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        boolean steep = dy > dx;

        if (steep) {
            int temp = dx;
            dx = dy;
            dy = temp;
        }

        int error = dx / 2;
        int x = x0;
        int y = y0;

        for (int i = 0; i <= dx; i++) {
            if (steep) {
                drawPixel(g, y, x, centerX, centerY);
            } else {
                drawPixel(g, x, y, centerX, centerY);
            }

            error -= dy;
            if (error < 0) {
                y += sy;
                error += dx;
            }
            x += sx;
        }
    }



    private void drawCircleBresenham(Graphics g, int x_center, int y_center, int radius, int centerX, int centerY) {
        g.setColor(Color.BLACK);

        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        while (x <= y) {
            drawPixel(g, x_center + x, y_center + y, centerX, centerY);
            drawPixel(g, x_center - x, y_center + y, centerX, centerY);
            drawPixel(g, x_center + x, y_center - y, centerX, centerY);
            drawPixel(g, x_center - x, y_center - y, centerX, centerY);
            drawPixel(g, x_center + y, y_center + x, centerX, centerY);
            drawPixel(g, x_center - y, y_center + x, centerX, centerY);
            drawPixel(g, x_center + y, y_center - x, centerX, centerY);
            drawPixel(g, x_center - y, y_center - x, centerX, centerY);

            if (d < 0) {
                d += 4 * x + 6;
            } else {
                d += 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
    }

}