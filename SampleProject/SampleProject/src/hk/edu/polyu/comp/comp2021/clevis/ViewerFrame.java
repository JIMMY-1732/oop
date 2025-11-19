package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.Rectangle;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.Shape;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ViewerFrame extends JFrame {
    private final Clevis clevis;

    public ViewerFrame(Clevis clevis) {
        super("Clevis Viewer");
        this.clevis = clevis;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 640);
        setLocationRelativeTo(null);

        add(new CanvasPanel(), BorderLayout.CENTER);
    }

    public void refresh() { repaint(); }

    private class CanvasPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2f));

            for (Shape s : clevis.all()) {
                if (s instanceof Rectangle) {
                    //req2
                    Rectangle r = (Rectangle) s;
                    g2.draw(new Rectangle2D.Double(r.x(), r.y(), r.w(), r.h()));
                } else if (s instanceof Line) {
                    //req3
                    Line l = (Line) s;
                    g2.draw(new Line2D.Double(l.x1(), l.y1(), l.x2(), l.y2()));
                } else if (s instanceof Circle) {
                    //req4
                    Circle c = (Circle) s;
                    double d = 2 * c.r();
                    g2.draw(new Ellipse2D.Double(c.cx() - c.r(), c.cy() - c.r(), d, d));
                } else if (s instanceof Square) {
                    //req5
                    Square sq = (Square) s;
                    g2.draw(new Rectangle2D.Double(sq.x(), sq.y(), sq.s(), sq.s()));
                }
            }
            g2.dispose();
        }
    }
}
