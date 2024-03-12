package graphics;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Use Python notebook instead to display results
 */
@Deprecated
public class GraphPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -4206770463206056562L;
    List<Double> data;
    double dataMax;
    double offY;
    double offX;

    @Deprecated
    public GraphPanel(List<Double> data) {
        super();
        this.data = data;
        this.offX = 20;
        this.offY = 15;

        if (data.isEmpty())
            dataMax = 0;
        else {
            dataMax = data.get(0);
            for (Double d : data) {
                if (d > dataMax)
                    dataMax = d;
            }
        }
    }

    public void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        //g.setStroke(new BasicStroke(2));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect((int) offX, (int) offY, this.getWidth() - (int) (2 * offX), this.getHeight() - (int) (2 * offY));
        g.setColor(Color.BLACK);
        g.drawLine((int) offX, (int) (this.getHeight() - offY), (int) offX, (int) offY);
        g.drawLine((int) offX, (int) (this.getHeight() - offY), (int) (this.getWidth() - offX), (int) (this.getHeight() - offY));
        if (dataMax != 0) {
            double stepX = (this.getWidth() - 2 * offY) / data.size();
            double x1 = offX - stepX, y1 = offY, x2 = offX, y2 = offY;
            for (int i = 1; i < data.size(); i++) {
                x2 += stepX;
                x1 += stepX;
                y2 = this.getHeight() - ((data.get(i) / dataMax) * (this.getHeight() - 2 * offY)) - offY;
                y1 = this.getHeight() - ((data.get(i - 1) / dataMax) * (this.getHeight() - 2 * offY)) - offY;
                //System.out.println(y2) ;
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                if (i % (data.size() / 10) == 0)
                    g.drawString("" + i, (int) x2, this.getHeight());
            }
            int gradY = 6;
            for (int i = 1; i < gradY; i++) {
                g.drawString("" + (i * (dataMax / gradY)), 0, (int) (this.getHeight() - offY - i * (this.getHeight() - 2 * offY) / gradY));
            }

        }


    }

}
