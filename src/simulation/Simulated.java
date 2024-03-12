package simulation;

public abstract class Simulated {
    protected final Simulation simulation;

    public Simulated(Simulation simulation) {
        this.simulation = simulation;
    }

    public abstract void initSimulation();

    public abstract void stepSimulation(double energyAvailable);
}
