package model;

import simulation.SimulatedCaptor;
import simulation.Simulation;

public class Captor extends Device {
    protected double pc = 1; // collision probability TODO probability of NO collision?

    public Captor(double pc, int ep) {
        super(ep);

        this.pc = pc;
    }

    @Override
    public double getInstantThroughput() {
        return this.commFrequency * pc;
    }

    @Override
    public SimulatedCaptor createSimulated(Simulation simulation) {
        return new SimulatedCaptor(simulation, this);
    }

    @Override
    public void updateParameters() {
    }
}
