import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.security.KeyPair;
import java.util.ArrayList;

public class ImageEdit
{
    // Режим рисования
    int     rezhim                   = 0;
    int     xPad;
    int     xf;
    int     yf;
    int     yPad;
    int     gridWidth                = 30;
    boolean pressed                  = false;
    long    startTime, endTime, SbSt = 0, Brt = 0;
    // текущий цвет
    Color           maincolor;
    MyFrame         f;
    MyPanel         japan;
    BufferedImage   imag;
    ArrayList<Pair> beginLSbS = new ArrayList<>();
    ArrayList<Pair> endLSbS   = new ArrayList<>();
    ArrayList<Pair> beginLBr  = new ArrayList<>();
    ArrayList<Pair> endLBr    = new ArrayList<>();
    ArrayList<Pair> beginE    = new ArrayList<>();
    ArrayList<Pair> endE      = new ArrayList<>();
    int             width, height;

    public ImageEdit()
    {
        f = new MyFrame("Графический редактор");
        f.setSize(350, 350);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        f.setJMenuBar(menuBar);
        menuBar.setBounds(0, 0, 350, 30);

        JMenu gridMenu = new JMenu("Сетка");
        menuBar.add(gridMenu);

        Action gridAction = new AbstractAction("Ширина")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    gridWidth = Integer.parseInt(JOptionPane.showInputDialog("Введите значение:"));
                    Graphics2D g2 = (Graphics2D) imag.getGraphics();
                    g2.setColor(Color.white);
                    g2.fillRect(0, 0, japan.getWidth(), japan.getHeight());
                    drawGrid(g2);
                    japan.repaint();
                } catch (Exception ignored)
                {
                }
            }
        };

        JMenuItem widthMenu = new JMenuItem(gridAction);
        gridMenu.add(widthMenu);

        JMenu lineMenu = new JMenu("Линия");
        menuBar.add(lineMenu);

        Action SbSAction = new AbstractAction("Пошаговая")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    String input = JOptionPane.showInputDialog("Введите x1 y1 x2 y2:");

                    if (input != null && !input.isEmpty())
                    {
                        String[] numbers = input.split(" ");
                        if (numbers.length == 4)
                        {
                            int x1 = Integer.parseInt(numbers[0]);
                            int y1 = Integer.parseInt(numbers[1]);
                            int x2 = Integer.parseInt(numbers[2]);
                            int y2 = Integer.parseInt(numbers[3]);

                            beginLSbS.add(new Pair((double) x1 / width * gridWidth, (double) y1 / height * gridWidth));
                            endLSbS.add(new Pair((double) x2 / width * gridWidth, (double) y2 / height * gridWidth));

                            drawLineSbS((Graphics2D) imag.getGraphics(), x1, y1, x2, y2, true);

                            startTime = System.nanoTime();
                            drawLineSbS((Graphics2D) imag.getGraphics(), x1 * gridWidth, y1 * gridWidth,
                                    x2 * gridWidth / 4, y2 * gridWidth / 4, false);
                            endTime = System.nanoTime();
                            SbSt    = endTime - startTime;
                            japan.repaint();
                        }
                    }
                } catch (Exception ignored)
                {
                }
            }
        };

        Action BrAction = new AbstractAction("Брезенхем")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    String input = JOptionPane.showInputDialog("Введите x1 y1 x2 y2:");

                    if (input != null && !input.isEmpty())
                    {
                        String[] numbers = input.split(" ");
                        if (numbers.length == 4)
                        {
                            int x1 = Integer.parseInt(numbers[0]);
                            int y1 = Integer.parseInt(numbers[1]);
                            int y2 = Integer.parseInt(numbers[2]);
                            int x2 = Integer.parseInt(numbers[3]);

                            beginLBr.add(new Pair((double) x1 / width * gridWidth, (double) y1 / height * gridWidth));
                            endLBr.add(new Pair((double) x2 / width * gridWidth, (double) y2 / height * gridWidth));

                            drawLineBr((Graphics2D) imag.getGraphics(), x1, y1, x2, y2, true);

                            startTime = System.nanoTime();
                            drawLineBr((Graphics2D) imag.getGraphics(), x1 * gridWidth, y1 * gridWidth,
                                    x2 * gridWidth, y2 * gridWidth, false);
                            endTime = System.nanoTime();
                            Brt     = endTime - startTime;
                            japan.repaint();
                        }
                    }
                } catch (Exception ignored)
                {
                }
            }
        };

        Action timeAction = new AbstractAction("Время")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    JOptionPane.showMessageDialog(null, "Пошаговый метод: " + SbSt + " ns\n" +
                            "Метод Брезенхема: " + Brt + " ns");
                } catch (Exception ignored)
                {
                }
            }
        };

        JMenuItem SbSMenu  = new JMenuItem(SbSAction);
        JMenuItem BrMenu   = new JMenuItem(BrAction);
        JMenuItem TimeMenu = new JMenuItem(timeAction);

        lineMenu.add(SbSMenu);
        lineMenu.add(BrMenu);
        lineMenu.add(TimeMenu);

        japan = new MyPanel();
        japan.setBounds(30, 30, 260, 260);
        japan.setBackground(Color.white);
        japan.setOpaque(true);
        f.add(japan);

        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.VERTICAL);

        JButton penbutton = new JButton("D");
        penbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                rezhim = 0;
            }
        });
        toolbar.add(penbutton);

        JButton lasticbutton = new JButton("C");
        lasticbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                rezhim = 1;
            }
        });
        toolbar.add(lasticbutton);

        JButton SbSbutton = new JButton("S");
        SbSbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                rezhim = 2;
            }
        });
        toolbar.add(SbSbutton);

        JButton linebutton = new JButton("B");
        linebutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                rezhim = 4;
            }
        });
        toolbar.add(linebutton);

        toolbar.setBounds(0, 0, 30, 300);
        f.add(toolbar);

        japan.addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent e)
            {
                if (pressed)
                {
                    Graphics   g  = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(maincolor);
                    switch (rezhim)
                    {
                        // карандаш
                        case 0:
                            drawLineBr(g2, xPad, yPad, e.getX(), e.getY(), false);
                            break;
                        // ластик
                        case 1:
                            clearArrays();
                            g2.setStroke(new BasicStroke(1000000.0f));
                            drawLineBr(g2, xPad, yPad, e.getX(), e.getY(), false);
                            drawGrid(g2);
                            break;
                    }
                    xPad = e.getX();
                    yPad = e.getY();
                }
                japan.repaint();
            }
        });
        japan.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {

                Graphics   g  = imag.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(maincolor);
                switch (rezhim)
                {
                    // карандаш
                    case 0:
                        drawLineBr(g2, xPad, yPad, xPad, yPad, false);
                        break;
                    // ластик
                    case 1:
                        clearArrays();
                        g2.setStroke(new BasicStroke(1000000.0f));
                        drawLineBr(g2, xPad, yPad, xPad, yPad, false);
                        drawGrid(g2);
                        break;
                }
                xPad = e.getX();
                yPad = e.getY();

                pressed = true;
                japan.repaint();
            }

            public void mousePressed(MouseEvent e)
            {
                xPad    = e.getX();
                yPad    = e.getY();
                xf      = e.getX();
                yf      = e.getY();
                pressed = true;
            }

            public void mouseReleased(MouseEvent e)
            {

                Graphics   g  = imag.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(maincolor);
                switch (rezhim)
                {
                    // ластик
                    case 1:
                        clearArrays();
                        g2.setStroke(new BasicStroke(1000000.0f));
                        drawLineBr(g2, xPad, yPad, xPad, yPad, false);
                        drawGrid(g2);
                        break;
                    // линия Step by Step
                    case 2:
                        beginLSbS.add(new Pair((double) xf / width, (double) yf / height));
                        endLSbS.add(new Pair((double) e.getX() / width, (double) e.getY() / height));

                        drawLineSbS(g2, xf / gridWidth, yf / gridWidth,
                                e.getX() / gridWidth, e.getY() / gridWidth, true);
                        drawLineSbS(g2, xf, yf, e.getX(), e.getY(), false);
                        break;
                    // линия Brezenhem
                    case 4:
                        beginLBr.add(new Pair((double) xf / width, (double) yf / height));
                        endLBr.add(new Pair((double) e.getX() / width, (double) e.getY() / height));
                        drawLineBr(g2, xf / gridWidth, yf / gridWidth,
                                e.getX() / gridWidth, e.getY() / gridWidth, true);

                        drawLineBr(g2, xf, yf, e.getX(), e.getY(), false);
                        break;
                }
                xf      = 0;
                yf      = 0;
                pressed = false;
                japan.repaint();
            }
        });

        f.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                japan.setSize(f.getWidth() - 40, f.getHeight() - 80);
                BufferedImage tempImage = new BufferedImage(japan.getWidth(), japan.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D    d2        = tempImage.createGraphics();
                d2.setColor(Color.white);
                d2.fillRect(0, 0, japan.getWidth(), japan.getHeight());
                width  = japan.getWidth();
                height = japan.getHeight();
                d2.setColor(Color.black);
                d2.setStroke(new BasicStroke(1.0f));
                redrawAll(d2);
                drawGrid(d2);
                imag = tempImage;
                japan.repaint();
            }
        });
        f.setLayout(null);
        f.setVisible(true);
    }

    public static void main(String[] args)
    {

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new ImageEdit();
            }
        });
    }

    public void drawLineSbS(Graphics2D g2, int x1, int y1, int x2, int y2, boolean bigD)
    {
        if (x1 == x2 && y1 == y2)
        {
            pixel(g2, x2, y2, bigD);
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        double k = (double) dy / dx;
        double b = y1 - k * x1;

        if (Math.abs(dx) >= Math.abs(dy))
        {
            int x  = Math.min(x1, x2);
            int mx = Math.max(x1, x2);
            int y;

            while (x <= mx)
            {
                y = (int) Math.round(k * x + b);
                pixel(g2, x, y, bigD);
                x++;
            }
        } else
        {

            int y  = Math.min(y1, y2);
            int my = Math.max(y1, y2);
            int x;

            while (y <= my)
            {
                x = (int) Math.round(k * y + b);
                pixel(g2, x, y, bigD);
                y++;
            }
        }
    }

    public void drawLineBr(Graphics2D g2, int x1, int y1, int x2, int y2, boolean bigD)
    {
        int dx  = Math.abs(x2 - x1);
        int dy  = Math.abs(y2 - y1);
        int sx  = x1 < x2 ? 1 : -1;
        int sy  = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2)
        {
            pixel(g2, x1, y1, bigD);

            int err2 = 2 * err;

            if (err2 > -dy)
            {
                err -= dy;
                x1 += sx;
            }

            if (err2 < dx)
            {
                err += dx;
                y1 += sy;
            }
        }

        pixel(g2, x2, y2, bigD);
    }


    public void pixel(Graphics2D g2, int x, int y, boolean bigD)
    {
        if (!bigD)
        {
            if (rezhim != 1) g2.setColor(Color.red);
            else g2.setColor(Color.white);
            g2.drawLine(x, y, x, y);
            g2.setColor(Color.black);
        } else
        {
            g2.setColor(Color.black);
            g2.fillRect(x * gridWidth, y * gridWidth, gridWidth, gridWidth);
        }
    }

    public void redrawAll(Graphics2D g2)
    {
        for (int i = 0; i < beginLSbS.size(); ++i)
        {
            drawLineSbS(g2, (int) (beginLSbS.get(i).first * width / gridWidth), (int) (beginLSbS.get(i).second * height / gridWidth),
                    (int) (endLSbS.get(i).first * width / gridWidth), (int) (endLSbS.get(i).second * height / gridWidth), true);
            drawLineSbS(g2, (int) (beginLSbS.get(i).first * width), (int) (beginLSbS.get(i).second * height),
                    (int) (endLSbS.get(i).first * width), (int) (endLSbS.get(i).second * height), false);
        }

        for (int i = 0; i < beginLBr.size(); ++i)
        {
            drawLineBr(g2, (int) (beginLBr.get(i).first * width / gridWidth), (int) (beginLBr.get(i).second * height / gridWidth),
                    (int) (endLBr.get(i).first * width / gridWidth), (int) (endLBr.get(i).second * height / gridWidth), true);
            drawLineBr(g2, (int) (beginLBr.get(i).first * width), (int) (beginLBr.get(i).second * height),
                    (int) (endLBr.get(i).first * width), (int) (endLBr.get(i).second * height), false);
        }
    }

    public void drawGrid(Graphics2D g2)
    {
        if (gridWidth <= 0) return;
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(1.0f));
        for (int x = 0; x <= width; x += gridWidth)
        {
            g2.drawLine(x, 0, x, height);
        }

        for (int y = 0; y <= height; y += gridWidth)
        {
            g2.drawLine(0, y, width, y);
        }

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(3.0f));

        g2.drawLine(0, 0, width, 0);
        g2.drawLine(width - 15, 10, width - 5, 0);

        g2.drawLine(0, 0, 0, height);
        g2.drawLine(10, height - 20, 0, height - 10);

        int tickSpacingX = width / 4;
        for (int i = 1; i < 4; i++)
        {
            int x     = (i * tickSpacingX / gridWidth) * gridWidth;
            int label = x / gridWidth;
            g2.drawLine(x, 0, x, 5);
            g2.drawString(Integer.toString(label), x + gridWidth / 3, 20);
        }

        int tickSpacingY = height / 4;
        for (int i = 1; i < 4; i++)
        {
            int y     = (i * tickSpacingY / gridWidth) * gridWidth;
            int label = y / gridWidth;
            g2.drawLine(0, y, 5, y);
            g2.drawString(Integer.toString(label), 10, y + gridWidth / 3);
        }

        g2.drawString("X", width - 20, 20);
        g2.drawString("Y", 10, height - 30);

        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(Color.black);
    }

    public void clearArrays()
    {
        beginLSbS.clear();
        endLSbS.clear();
        beginLBr.clear();
        endLBr.clear();
        beginE.clear();
        endE.clear();
    }

    class MyFrame extends JFrame
    {
        public MyFrame(String title)
        {
            super(title);
        }

        public void paint(Graphics g)
        {
            super.paint(g);
        }
    }

    class MyPanel extends JPanel
    {
        public MyPanel()
        {}

        public void paintComponent(Graphics g)
        {
            if (imag == null)
            {
                imag = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D d2 = imag.createGraphics();
                d2.setColor(Color.white);
                d2.fillRect(0, 0, this.getWidth(), this.getHeight());
                drawGrid(d2);
            }
            super.paintComponent(g);
            g.drawImage(imag, 0, 0, this);
        }
    }

    class Pair
    {

        double first;
        double second;

        public Pair(double first, double second)
        {
            this.first  = first;
            this.second = second;
        }
    }
}