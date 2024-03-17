package simulation;

import model.Device;
import simulation.data.DataColumn;

public abstract class SimulatedDevice extends Simulated {
    public DataColumn energyRecord, throughputRecord, downtimeRecord;
    //public DataColumn frequencyRecord;

    protected SimulatedDevice(Simulation simulation) {
        super(simulation);

        final String friendlyName = getClass().getSimpleName().replace("Simulated", "");
        this.energyRecord = simulation.table.newColumn(friendlyName + " Energy (kJ)");
        //this.frequencyRecord = simulation.table.newColumn(friendlyName + " Frequency (Hz)");
        this.throughputRecord = simulation.table.newColumn(friendlyName + " Throughput (pkt/s)");
        this.downtimeRecord = simulation.table.newColumn(friendlyName + " Downtime (s)");
    }

    @Override
    public void stepSimulation(double energyAvailable) {
        final double effectiveEnergy = energyAvailable * getDevice().exposure;

        this.getDevice().addEnergyData(effectiveEnergy, simulation.spf);
        this.getDevice().updateParameters();
        this.getDevice().updateEnergy(effectiveEnergy, simulation.spf);

        energyRecord.addNewData(getDevice().getEnergy() / 1e3);
        //frequencyRecord.addNewData(getDevice().getCommunicationFrequency());
        throughputRecord.addNewData(getDevice().getInstantThroughput());
        downtimeRecord.addNewData(getDevice().energy == 0 ? 1 : 0);
    }

    protected abstract Device getDevice();

    @Override
    public void initSimulation() {
        this.energyRecord.addNewData(getDevice().getEnergy());
        //this.frequencyRecord.addNewData(getDevice().getCommunicationFrequency());
        this.throughputRecord.addNewData(getDevice().getInstantThroughput());
        this.throughputRecord.addNewData(getDevice().energy == 0 ? 1 : 0);
    }
}
