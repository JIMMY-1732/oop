package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;


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

            for (Clevis.Shape s : clevis.all()) {
                if (s instanceof Clevis.Rectangle) {
                    //req2
                    Clevis.Rectangle r = (Clevis.Rectangle) s;
                    g2.draw(new Rectangle2D.Double(r.x(), r.y(), r.w(), r.h()));
                } else if (s instanceof Clevis.Line) {
                    //req3
                    Clevis.Line l = (Clevis.Line) s;
                    g2.draw(new Line2D.Double(l.x1(), l.y1(), l.x2(), l.y2()));
                } else if (s instanceof Clevis.Circle) {
                    //req4
                    Clevis.Circle c = (Clevis.Circle) s;
                    double d = 2 * c.r();
                    g2.draw(new Ellipse2D.Double(c.cx() - c.r(), c.cy() - c.r(), d, d));
                } else if (s instanceof Clevis.Square) {
                    //req5
                    Clevis.Square sq = (Clevis.Square) s;
                    g2.draw(new Rectangle2D.Double(sq.x(), sq.y(), sq.s(), sq.s()));
                }
            }
            g2.dispose();
        }
    }
}
