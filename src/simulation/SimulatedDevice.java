package simulation;

import model.Device;
import simulation.data.DataColumn;

public abstract class SimulatedDevice extends Simulated {
    public DataColumn energyRecord, frequencyRecord, throughputRecord;

    protected SimulatedDevice(Simulation simulation) {
        super(simulation);

        final String friendlyName = getClass().getSimpleName().replace("Simulated", "");
        this.energyRecord = simulation.table.newColumn(friendlyName + " Energy (J)");
        this.frequencyRecord = simulation.table.newColumn(friendlyName + " Frequency (Hz)");
        this.throughputRecord = simulation.table.newColumn(friendlyName + " Throughput (pkt/s)");
    }

    @Override
    public void stepSimulation(double energyAvailable) {
        final double effectiveEnergy = energyAvailable * getDevice().exposure;

        this.getDevice().addEnergyData(effectiveEnergy, simulation.spf);
        this.getDevice().updateParameters();
        this.getDevice().updateEnergy(effectiveEnergy, simulation.spf);

        energyRecord.addNewData(getDevice().getEnergy());
        frequencyRecord.addNewData(getDevice().getInstantThroughput());
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
