package model;

import simulation.Simulated;
import simulation.Simulation;

public interface Simulable {
    public Simulated createSimulated(Simulation simulation);

    void updateParameters();
}
