package simulation;

import model.Cluster;
import model.Simulable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulatedCluster extends Simulated {
    protected final List<Simulated> simulated;
    protected final Cluster cluster;

    public SimulatedCluster(Simulation simulation, Cluster cluster) {
        super(simulation);

        this.cluster = cluster;

        final List<Simulated> simulated = new ArrayList<>();
        for (Simulable s : cluster.getSimulables())
            simulated.add(s.createSimulated(simulation));
        this.simulated = Collections.unmodifiableList(simulated);
    }

    @Override
    public void stepSimul(double energyAvailable) {
        cluster.updateParameters();
        for (Simulated s : simulated)
            s.stepSimul(energyAvailable);
    }

    @Override
    public void initData() {
        for (Simulated s : simulated)
            s.initData();
    }
}
