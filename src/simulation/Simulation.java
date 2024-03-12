package simulation;

import model.Simulable;
import model.Sink;
import model.energy.EnergyArrivalModel;
import simulation.data.DataColumn;
import simulation.data.DataTable;

import java.io.File;
import java.util.List;

public class Simulation {
    public final int tStart, tEnd;
    int duration = 1;
    Sink sink;
    private boolean realData = true;
    private Simulated sim;
    public final double spf;
    private EnergyArrivalModel eModel;
    public final DataTable table;
    public final DataColumn energyArrivalRecord, timeRecord;

    public Simulation(int tStart, int tEnd, Simulable sim, double spf, EnergyArrivalModel eModel) {
        this.tStart = tStart;
        this.tEnd = tEnd;
        duration = (this.tEnd - this.tStart) / 1440;
        this.eModel = eModel;
        this.table = new DataTable(this);
        this.timeRecord = table.newColumn("Time (s)");
        this.energyArrivalRecord = table.newColumn("Energy Profile (W/m2)");
        this.sim = sim.createSimulated(this);
        this.spf = spf;
    }

    public void simulate(boolean realData, Sink s) {
        //INIT DATA
        sim.initData();

        this.realData = realData;
        this.sink = s;

        // SIMULATE FROM T_START TO T_END
        for (int j = tStart + 1; j < tEnd; j++) {
            final double energyAvailable = eModel.getEnergy(j * spf);
            sim.stepSimul(energyAvailable);
            energyArrivalRecord.addNewData(energyAvailable);
            timeRecord.addNewData(toDays(j - 1));
        }
    }

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    private double toDays(int time) {
        return (double) time / SECONDS_PER_DAY;
    }

    public double getSpf() {
        return spf;
    }

    @Deprecated
    public static double getArea(List<Double> ar) {
        double result = 0;
        for (int i = 1; i < ar.size(); i++) {
            result += (ar.get(i - 1) + ar.get(i)) / 2;
        }
        return result;
    }

    public void exportToCsv() {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(realData ? "realdata_" : "simdata_");
        nameBuilder.append(duration + "d_").append(sink.batterySize + "b_");

        switch (sink.mode) {
            case 0:
                nameBuilder.append("const_c_eq_" + (String.format("%1.1e", sink.c)));
                break;
            case 1:
                nameBuilder.append("prop_a_eq_" + (String.format("%1.1e", sink.a)));
                break;
            case 2:
                nameBuilder.append(sink.version.name());
                nameBuilder.append("_" + sink.periodMax + "sw");
                break;
        }

        System.out.println("Exporting " + nameBuilder + "...");
        table.export(new File("output/" + nameBuilder + ".csv"));
    }
}
