package simulation;

import model.Cluster;
import model.Sink;
import model.energy.EnergyArrivalModel;
import simulation.data.DataColumn;
import simulation.data.DataTable;

import java.io.File;
import java.util.List;

public class Simulation {
    public final int tStart, tEnd, duration;
    private boolean realData = true;
    private final SimulatedCluster cluster;
    public final double spf;
    private final EnergyArrivalModel eModel;
    public final DataTable table;
    public final DataColumn energyArrivalRecord, timeRecord;

    public Simulation(int tStart, int tEnd, Cluster cluster, double spf, EnergyArrivalModel eModel) {
        this.tStart = tStart;
        this.tEnd = tEnd;
        this.duration = (tEnd - tStart) / 1440;
        this.eModel = eModel;
        this.table = new DataTable(this);
        this.timeRecord = table.newColumn("Time (s)");
        this.energyArrivalRecord = table.newColumn("Energy Profile (W/m2)");
        this.cluster = cluster.createSimulated(this);
        this.spf = spf;
    }

    public void run(boolean realData) {
        //INIT DATA
        cluster.initSimulation();

        this.realData = realData;

        // SIMULATE FROM T_START TO T_END
        for (int j = tStart + 1; j < tEnd; j++) {
            final double energyAvailable = eModel.getEnergy(j * spf);
            cluster.stepSimulation(energyAvailable);
            energyArrivalRecord.addNewData(energyAvailable);
            timeRecord.addNewData(toDays(j - 1));
        }
    }

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    private double toDays(int time) {
        return (double) time / SECONDS_PER_DAY;
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
        final Sink sink = cluster.getCluster().getSink();

        final StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(realData ? "realdata_" : "simdata_");
        nameBuilder.append(duration + "d_").append((int) Math.floor(sink.batterySize) + "b_");

        switch (sink.mode) {
            case CONSTANT_FREQUENCY:
                nameBuilder.append("const_c_eq_" + (String.format("%1.1e", sink.c)));
                break;
            case PROPORTIONAL_FREQUENCY:
                nameBuilder.append("prop_a_eq_" + (String.format("%1.1e", sink.a)));
                break;
            case ODMACPP_GB:
            case ODMACPP_SLB:
                nameBuilder.append(sink.mode.name().toLowerCase());
                nameBuilder.append("_" + sink.periodMax + "sw");
                break;
        }

        System.out.println("Exporting " + nameBuilder + "...");
        table.export(new File("output/" + nameBuilder + ".csv"));
    }
}
