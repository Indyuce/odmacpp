import graphics.GraphFrame;
import model.Cluster;
import model.DeviceMode;
import model.energy.EnergyArrivalModel;
import simulation.Simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        double secondPerFrame = 60;
        boolean realData = true;
        double secondPerPeriod = 24 * 60 * 60 / secondPerFrame;
        boolean memo = true;

        //    generateDataPaper(memo, secondPerPeriod, secondPerFrame);

        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame);
        Cluster h = new Cluster(2, eModel.getEnergyPeriod(), DeviceMode.ODMACPP_GB);
        h.getCaptors().get(0).exposure = 1;
        h.getCaptors().get(0).batterySize = 40000;
        h.getCaptors().get(1).exposure = .4;
        h.getCaptors().get(0).batterySize = 20000;
        Simulation simul = new Simulation(0, 60 * 24 * 90, h, secondPerFrame, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void generateDataPaper(boolean memo, double secondPerPeriod, double secondPerFrame) {
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 0.5);
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 5);
        simDataConst(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 20);

        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 1d / 100000d);
        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 1d / 5000d);
        simDataProp(memo, false, 7, 40000, secondPerPeriod, secondPerFrame, 1d);

        simDataODMACPP(memo, false, 30, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, false, 30, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_GB, 90);

        simDataProp(memo, true, 30, 40000, secondPerPeriod, secondPerFrame, 1d / 5000d);

        simDataODMACPP(memo, true, 30, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, true, 30, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_GB, 90);

        simDataODMACPP(memo, true, 30, 10000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_SLB, 90);
        simDataODMACPP(memo, true, 30, 10000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_GB, 90);

        simDataODMACPP(memo, true, 100, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_GB, 100);
        simDataODMACPP(memo, true, 100, 40000, secondPerPeriod, secondPerFrame, DeviceMode.ODMACPP_GB, 15);
    }

    public static void simDataProp(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondPerFrame, double a) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), DeviceMode.PROPORTIONAL_FREQUENCY);
        h.getSink().batterySize = maxBattery;
        h.getSink().a = a;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondPerFrame, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void simDataConst(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondPerFrame, double c) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), DeviceMode.CONSTANT_FREQUENCY);
        h.getSink().batterySize = maxBattery;
        h.getSink().c = c;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondPerFrame, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static void simDataODMACPP(boolean memo, boolean realData, int duration, double maxBattery, double secondPerPeriod, double secondPerFrame, DeviceMode mode, int SW) {
        EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame);
        Cluster h = new Cluster(5, eModel.getEnergyPeriod(), mode);
        h.getSink().batterySize = maxBattery;
        h.getSink().periodMax = SW;
        Simulation simul = new Simulation(0, 60 * 24 * duration, h, secondPerFrame, eModel);
        simul.run();
        if (memo) simul.exportToCsv();
        else new GraphFrame(1024, 768, simul);
    }

    public static EnergyArrivalModel generateModel(boolean realData, double secondPerPeriod, double secondPerFrame) {

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
                    return Math.max(0, Math.sin(t * 2 * Math.PI / (secondPerPeriod * secondPerFrame)) + currGauss);
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
