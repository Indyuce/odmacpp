package graphics;

import simulation.Simulation;
import simulation.data.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Use Python notebook instead to display results
 */
@Deprecated
public class GraphFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -3206747177767130065L;
    private int width, height;
    private ArrayList<GraphPanel> graphPanels;

    @Deprecated
    public GraphFrame(int width, int height, Simulation sim) {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.height = height;
        this.width = width;
        this.setSize(width, height);
        this.setLayout(new GridLayout(3, 2));

        DataTable dataSet = sim.table;
        //System.out.println("FINAL ENERGY ON THE SINK = " + Simulation.getArea(dataSet.get(3))) ;
        //System.out.println("FINAL THROUGHPUT ON THE SINK = " + Simulation.getArea(dataSet.byIndex(3).data));

        graphPanels = new ArrayList<>(dataSet.data.size());
        graphPanels.add(new GraphPanel(dataSet.byIndex(0).data));
        graphPanels.add(new GraphPanel(dataSet.byIndex(1).data));
        graphPanels.add(new GraphPanel(dataSet.byIndex(3).data));

        // for(int i = 0 ; i < 1 ; i ++)
        //	 graphPanels.add(new GraphPanel(dataSet.get(i))) ;
        for (GraphPanel gp : graphPanels)
            this.add(gp);

        this.setVisible(true);


    }

}
