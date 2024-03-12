package model;

import model.energy.EnergyUser;
import simulation.SimulatedCluster;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Virtual device to simulate multiple devices at once
 */
public class Cluster implements EnergyUser, Simulable {
    private final Sink sink;
    private final List<Captor> captors;
    private final List<Simulable> simulables;
    final static double PC_INIT = 1;

    public Cluster(int nbCaptors, int ep, DeviceMode mode) {
        this.sink = new Sink(ep, mode, 100 * nbCaptors * PC_INIT);

        List<Captor> captors = new ArrayList<>(nbCaptors);
        for (int i = 0; i < nbCaptors; i++)
            captors.add(new Captor(PC_INIT, mode, ep));
        this.captors = Collections.unmodifiableList(captors);

        List<Simulable> simulables = new ArrayList<>(nbCaptors + 1);
        simulables.add(sink);
        simulables.addAll(captors);
        this.simulables = Collections.unmodifiableList(simulables);
    }

    public Sink getSink() {
        return sink;
    }

    @Override
    public SimulatedCluster createSimulated(Simulation simulation) {
        return new SimulatedCluster(simulation, this);
    }

    public Collection<Simulable> getSimulables() {
        return simulables;
    }

    @Override
    public void updateEnergy(double energyPower, double seconds) {
        sink.updateEnergy(energyPower, seconds);
        for (Captor c : captors)
            c.updateEnergy(energyPower, seconds);
    }

    public void updateParameters() {

        // Update captor collision probability
        final double activeCaptorNumber = Math.max(1, captors.stream().filter(captor -> captor.getEnergy() != 0).count());
        for (Captor c : captors) c.pc = 1 / activeCaptorNumber;
    }
}
