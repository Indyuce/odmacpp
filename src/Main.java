import graphics.GraphFrame;
import model.Cluster;
import model.Device;
import model.DeviceMode;
import model.energy.EnergyArrivalModel;
import simulation.SimulatedCaptor;
import simulation.SimulatedCluster;
import simulation.Simulation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // generateDataPaper(memo, secondPerPeriod, secondsPerTick);
        // wrtExposure();
        //wrtBatterySize();
        networkLifetime();
        //test();
    }

    public static void test() {
        double secondsPerTick = 60;
        boolean realData = true;
        double secondPerPeriod = 24 * 60 * 60 / secondsPerTick;
        boolean memo = true;
        int n_captors = 3;

        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
        Cluster h = new Cluster(n_captors, eModel.getEnergyPeriod(), DeviceMode.ODMACPP_GB);
        h.getCaptors().get(0).batterySize = 15000;
        h.getCaptors().get(0).exposure = .75;
        h.getCaptors().get(1).batterySize = 30000;
        h.getCaptors().get(0).exposure = .5;
        h.getCaptors().get(2).batterySize = 40000;
        Simulation simul = new Simulation(0, 60 * 24 * 90, h, secondsPerTick, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void networkLifetime() {
        double secondsPerTick = 60;
        boolean realData = true;
        double secondPerPeriod = 24 * 60 * 60 / secondsPerTick;
        boolean memo = true;
        int n_captors = 3;

        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
        Cluster h = new Cluster(n_captors, eModel.getEnergyPeriod(), DeviceMode.ODMACPP_GB);
        h.getCaptors().get(0).batterySize = 15000;
        h.getCaptors().get(0).exposure = .75;
        h.getCaptors().get(1).batterySize = 30000;
        h.getCaptors().get(0).exposure = .5;
        h.getCaptors().get(2).batterySize = 40000;
        Simulation simul = new Simulation(0, 60 * 24 * 90, h, secondsPerTick, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    private static void wrtExposure() {
        for (DeviceMode mode : DeviceMode.values()) {
            double secondsPerTick = 60;
            boolean realData = true;
            double secondPerPeriod = 24 * 60 * 60 / secondsPerTick;
            int n_captors = 100;
            double step = (double) 1 / (n_captors - 1);

            EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
            Cluster h = new Cluster(n_captors, eModel.getEnergyPeriod(), mode);
            for (int i = 0; i < n_captors; i++)
                h.getCaptors().get(i).exposure = i * step;
            Simulation simul = new Simulation(0, 60 * 24 * 90, h, secondsPerTick, eModel);
            simul.run();

            try {
                final File targetFile = new File("output/wrt_exposure_" + mode.name + ".csv");
                final PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
                for (int i = 0; i < n_captors; i++) {
                    final double x = h.getCaptors().get(i).exposure * 100;
                    pw.println(x + ";" + totalThroughput(i, simul.cluster, secondsPerTick) + ";" + totalDowntime(i, simul.cluster, secondsPerTick));
                }
                pw.close();
            } catch (FileNotFoundException exception) {
                System.exit(1);
            }
        }
    }

    private static void wrtBatterySize() {
        for (DeviceMode mode : DeviceMode.values()) {
            double secondsPerTick = 60;
            boolean realData = true;
            double secondPerPeriod = 24 * 60 * 60 / secondsPerTick;
            int n_captors = 10;
            double step = Device.DEFAULT_BATTERY_SIZE / (n_captors - 1);

            EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
            Cluster h = new Cluster(n_captors, eModel.getEnergyPeriod(), mode);
            for (int i = 0; i < n_captors; i++)
                h.getCaptors().get(i).batterySize = i * step;
            Simulation simul = new Simulation(0, 60 * 24 * 90, h, secondsPerTick, eModel);
            simul.run();

            try {
                final File targetFile = new File("output/wrt_capacity_" + mode.name + ".csv");
                final PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
                for (int i = 0; i < n_captors; i++) {
                    final double x = h.getCaptors().get(i).batterySize;
                    pw.println(x + ";" + totalThroughput(i, simul.cluster, secondsPerTick) + ";" + totalDowntime(i, simul.cluster, secondsPerTick));
                }
                pw.close();
            } catch (FileNotFoundException exception) {
                System.exit(1);
            }
        }
    }

    private static double totalThroughput(int i, SimulatedCluster cluster, double secondsPerTick) {
        return ((SimulatedCaptor) cluster.getSimulated().get(1 + i)).throughputRecord.integrate(secondsPerTick);
    }

    private static double totalDowntime(int i, SimulatedCluster cluster, double secondsPerTick) {
        return ((SimulatedCaptor) cluster.getSimulated().get(1 + i)).downtimeRecord.integrate(secondsPerTick);
    }

    public static void generateDataPaper(boolean memo, double secondPerPeriod, double secondsPerTick) {
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 0.5);
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 5);
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 20);

        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 1d / 100000d);
        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 1d / 5000d);
        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondsPerTick, 1d);

        simDataODMACPP(memo, false, 30, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, false, 30, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_GB, 90);

        simDataProp(memo, true, 30, 40000, secondPerPeriod, secondsPerTick, 1d / 5000d);

        simDataODMACPP(memo, true, 30, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, true, 30, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_GB, 90);

        simDataODMACPP(memo, true, 30, 10000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, true, 30, 10000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_GB, 90);

        simDataODMACPP(memo, true, 100, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_GB, 100);
        simDataODMACPP(memo, true, 100, 40000, secondPerPeriod, secondsPerTick, DeviceMode.ODMACPP_GB, 15);
    }

    public static void simDataProp(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondsPerTick, double a) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), DeviceMode.PROPORTIONAL_FREQUENCY);
        h.getSink().batterySize = maxBattery;
        h.getSink().alphaConstant = a;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondsPerTick, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void simDataConst(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondsPerTick, double c) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), DeviceMode.CONSTANT_FREQUENCY);
        h.getSink().batterySize = maxBattery;
        h.getSink().fConstant = c;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondsPerTick, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void simDataODMACPP(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondsPerTick, DeviceMode mode, int SW) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondsPerTick);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), mode);
        h.getSink().batterySize = maxBattery;
        h.getSink().periodMax = SW;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondsPerTick, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static EnergyArrivalModel generateModel(boolean realData, double secondPerPeriod, double secondsPerTick) {

        EnergyArrivalModel eModel;
        if (!realData) {
            eModel = new EnergyArrivalModel() {
                Random r = new Random();
                double currGauss = r.nextGaussian() / 10;
                int counter = 0;

                @Override
                public double getEnergy(double t) {
                    counter++;
                    if (counter == 60) {
                        currGauss = r.nextGaussian() / 4;
                        counter = 0;
                    }
                    return Math.max(0, Math.sin(t * 2 * Math.PI / (secondPerPeriod * secondsPerTick)) + currGauss);
                }

                @Override
                public int getEnergyPeriod() {
                    return (int) secondPerPeriod;
                }

                @Override
                public boolean isReal() {
                    return false;
                }
            };
        } else {
            eModel = new EnergyArrivalModel() {
                final double[] data = new double[40000];

                {
                    try {
                        BufferedReader r = new BufferedReader(new FileReader("data/data_mississipi.csv"));
                        String s;
                        int i = 0;
                        while ((s = r.readLine()) != null) {
                            String[] table = s.split(";");
                            data[i] = Math.max(0, Double.parseDouble(table[1]));
                            i++;
                        }
                        r.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }

                public double getEnergy(double t) {
                    return Math.max(0, data[(int) (t / (5 * 60))] * 0.2 / 100);
                }

                @Override
                public int getEnergyPeriod() {
                    return (int) secondPerPeriod;
                }

                @Override
                public boolean isReal() {
                    return true;
                }
            };
        }
        return eModel;
    }
}
