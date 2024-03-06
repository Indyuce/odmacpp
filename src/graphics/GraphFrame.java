package graphics;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import model.Simulation;

public class GraphFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -3206747177767130065L;
    private int width, height;
    private ArrayList<GraphPanel> graphPanels;

    public GraphFrame(int width, int height, Simulation sim) {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.height = height;
        this.width = width;
        this.setSize(width, height);
        this.setLayout(new GridLayout(3, 2));

        ArrayList<ArrayList<Double>> dataSet = sim.getData();
        //System.out.println("FINAL ENERGY ON THE SINK = " + Simulation.getArea(dataSet.get(3))) ;
        System.out.println("FINAL THROUGHPUT ON THE SINK = " + Simulation.getArea(dataSet.get(3)));

        graphPanels = new ArrayList<GraphPanel>(dataSet.size());
        graphPanels.add(new GraphPanel(dataSet.get(0)));
        graphPanels.add(new GraphPanel(dataSet.get(1)));
        graphPanels.add(new GraphPanel(dataSet.get(3)));

        // for(int i = 0 ; i < 1 ; i ++)
        //	 graphPanels.add(new GraphPanel(dataSet.get(i))) ;
        for (GraphPanel gp : graphPanels)
            this.add(gp);

        this.setVisible(true);


    }

}
