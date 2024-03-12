package model;

import java.util.ArrayList;

class SimulatedCluster extends Simulated {

    protected ArrayList<Simulated> simulated;
    protected Cluster cluster;
    protected ArrayList<ArrayList<Double>> dataSet;

    protected SimulatedCluster(Cluster cluster, double spf, int tStart, int tEnd) {
        super(spf);
        simulated = new ArrayList<Simulated>();
        this.cluster = cluster;
        for (Simulable s : cluster.getSimulables())
            simulated.add(s.createSimulated(spf, tStart, tEnd));
        dataSet = new ArrayList<ArrayList<Double>>();
        for (Simulated s : simulated)
            dataSet.addAll(s.getData());

    }

    public void stepSimul(int i, double energyAvailable) {
        cluster.updateParameters();
        for (Simulated s : simulated)
            s.stepSimul(i, energyAvailable);
    }

    @Override
    public void initData() {
        for (Simulated s : simulated)
            s.initData();
    }

    @Override
    public ArrayList<ArrayList<Double>> getData() {
        return dataSet;
    }

}
