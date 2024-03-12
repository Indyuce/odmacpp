package simulation;

import model.Device;
import simulation.data.DataColumn;

public abstract class SimulatedDevice extends Simulated {
    public DataColumn energyRecord, frequencyRecord, throughputRecord;

    protected SimulatedDevice(Simulation simulation) {
        super(simulation);

        this.energyRecord = simulation.table.newColumn(getClass().getSimpleName() + "_Energy (J)");
        this.frequencyRecord = simulation.table.newColumn(getClass().getSimpleName() + "_Frequency (Hz)");
        this.throughputRecord = simulation.table.newColumn(getClass().getSimpleName() + "_Throughput (pkt/s)");
    }

    @Override
    public void stepSimulation(double energyAvailable) {
        this.getDevice().addEnergyData(energyAvailable, simulation.spf);
        this.getDevice().updateParameters();
        this.getDevice().updateEnergy(energyAvailable, simulation.spf);

        energyRecord.addNewData(getDevice().getEnergy());
        frequencyRecord.addNewData(getDevice().getInstantThroughput()) ;
        throughputRecord.addNewData(getDevice().getInstantThroughput() * simulation.spf);
    }

    protected abstract Device getDevice();

    @Override
    public void initSimulation() {
        this.energyRecord.addNewData(getDevice().getEnergy());
        this.frequencyRecord.addNewData(getDevice().getCommunicationFrequency());
        this.throughputRecord.addNewData(getDevice().getInstantThroughput());
    }
}
