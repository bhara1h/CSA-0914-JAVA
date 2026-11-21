import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple AWT drawing pad. Strokes are kept in memory and repainted on demand.
 */
public class DrawPad extends Frame {
    public DrawPad() {
        super("DrawPad");
        setLayout(new BorderLayout());
        DrawCanvas canvas = new DrawCanvas();
        add(canvas, BorderLayout.CENTER);

        java.awt.Button clearButton = new java.awt.Button("Clear Drawing");
        clearButton.addActionListener(event -> canvas.clearDrawing());
        add(clearButton, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Canvas stores individual strokes so its paint method can redraw them
     * after expose events and resizing.
     */
    private static class DrawCanvas extends Canvas {
        private final List<List<Point>> strokes = new ArrayList<>();
        private List<Point> currentStroke;

        DrawCanvas() {
            setBackground(Color.WHITE);
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    currentStroke = new ArrayList<>();
                    currentStroke.add(event.getPoint());
                    strokes.add(currentStroke);
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent event) {
                    if (currentStroke != null) {
                        currentStroke.add(event.getPoint());
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent event) {
                    if (currentStroke != null) {
                        currentStroke.add(event.getPoint());
                        currentStroke = null;
                        repaint();
                    }
                }
            };
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        void clearDrawing() {
            strokes.clear();
            currentStroke = null;
            repaint();
        }

        @Override
        public void paint(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setColor(Color.BLACK);
            graphics2D.setStroke(new java.awt.BasicStroke(3,
                    java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            for (List<Point> stroke : strokes) {
                if (stroke.size() == 1) {
                    Point point = stroke.get(0);
                    graphics2D.fillOval(point.x - 1, point.y - 1, 3, 3);
                } else {
                    for (int index = 1; index < stroke.size(); index++) {
                        Point previous = stroke.get(index - 1);
                        Point current = stroke.get(index);
                        graphics2D.drawLine(previous.x, previous.y, current.x, current.y);
                    }
                }
            }
            graphics2D.dispose();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new DrawPad().setVisible(true));
    }
}
