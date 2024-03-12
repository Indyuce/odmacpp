package simulation;

import model.Sink;

public class SimulatedSink extends SimulatedDevice {

    Sink sink;

    public SimulatedSink(Simulation simulation, Sink s) {
        super(simulation);

        this.sink = s;
    }

    @Override
    public Sink getDevice() {
        return sink;
    }
}
