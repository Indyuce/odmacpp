package simulation;

import model.Captor;
import model.Cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Could be merged with {@link Simulation}
 */
public class SimulatedCluster extends Simulated {
    protected final Cluster cluster;
    protected final List<Simulated> simulated;

    public SimulatedCluster(Simulation simulation, Cluster cluster) {
        super(simulation);

        this.cluster = cluster;

        final List<Simulated> simulated = new ArrayList<>();
        simulated.add(cluster.getSink().createSimulated(simulation));
        for (Captor captor : cluster.getCaptors()) simulated.add(captor.createSimulated(simulation));
        this.simulated = Collections.unmodifiableList(simulated);
    }

    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public void stepSimulation(double energyAvailable) {
        cluster.updateParameters();
        for (Simulated s : simulated)
            s.stepSimulation(energyAvailable);
    }

    @Override
    public void initSimulation() {
        for (Simulated s : simulated)
            s.initSimulation();
    }
}
