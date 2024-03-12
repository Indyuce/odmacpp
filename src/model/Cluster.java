package model;

import model.energy.EnergyUser;

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

    public Cluster(int nbCaptors, int ep, int mode) {
        this.sink = new Sink(ep, mode, 100 * nbCaptors * PC_INIT);

        List<Captor> captors = new ArrayList<>(nbCaptors);
        for (int i = 0; i < nbCaptors; i++)
            captors.add(new Captor(PC_INIT, ep));
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
    public Simulated createSimulated(double spf, int tStart, int tEnd) {
        return new SimulatedCluster(this, spf, tStart, tEnd);
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
        double activeNumber = 0;
        for (Captor c : captors)
            if (c.getEnergy() != 0)
                activeNumber++;

        for (Captor c : captors)
            c.pc = 1 / activeNumber;
    }
}
