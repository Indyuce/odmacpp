package model;

import simulation.SimulatedSink;
import simulation.Simulation;

public class Sink extends Device {
    public Sink(int ep, DeviceMode mode, double commFreqMax) {
        super(ep, mode, commFreqMax);
    }

    @Override
    public double getInstantThroughput() {
        // No collision possible as there is only one downlink sink channel
        return this.commFrequency;
    }

    @Override
    public SimulatedSink createSimulated(Simulation simulation) {
        return new SimulatedSink(simulation, this);
    }
}
