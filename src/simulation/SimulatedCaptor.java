package simulation;

import model.Captor;

public class SimulatedCaptor extends SimulatedDevice {
    private final Captor captor;

    public SimulatedCaptor(Simulation simulation, Captor captor) {
        super(simulation);

        this.captor = captor;
    }

    @Override
    public Captor getDevice() {
        return captor;
    }
}
