package model;

import model.energy.EnergyUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Device implements EnergyUser, Simulable {

    protected double energy;
    protected double commFrequency = COMM_FREQUENCY_INIT;

    /**
     * Size of device battery (J)
     */
    public double batterySize;

    /**
     * Maximum communication frequency (Hz)
     */
    protected final double maxCommFrequency;
    protected final int energyPeriod;
    protected final List<Double> energySum, energySumCarre;
    protected final List<LinkedList<Double>> energyData;
    protected int availableEnergy = 0;
    protected int counterEnergy = 0;
    protected double periodNumber = 0;
    public int periodMax = 90;
    public final DeviceMode mode;

    /**
     * ODMAC++ computed optimal communication frequency (Hz)
     */
    protected double cfm = 0;

    /**
     * Communication cost (W/Hz), has to be multiplied
     * by the actual communication frequency to provide a draw.
     */
    public static final double COMMUNICATION_COST = 0.1;

    /**
     * Idle device draw (W)
     */
    public static final double idleCost = 0.1;

    public static final double DEFAULT_MAX_COMM_FREQUENCY = 500;
    public static final double DEFAULT_ENERGY_RATIO = 500;
    public static final double COMM_FREQUENCY_INIT = 0;
    public static final double DEFAULT_BATTERY_SIZE = 40000;

    public Device(int energyPeriod, DeviceMode mode) {
        this(energyPeriod, mode, DEFAULT_MAX_COMM_FREQUENCY);
    }

    public Device(int energyPeriod, DeviceMode mode, double maxCommFrequency) {
        this.energyPeriod = energyPeriod;
        this.mode = mode;
        this.energySum = new ArrayList<>(energyPeriod);
        this.energySumCarre = new ArrayList<>(energyPeriod);
        this.energyData = new ArrayList<>();
        for (int i = 0; i < energyPeriod; i++) this.energyData.add(new LinkedList<>());
        for (int i = 0; i < energyPeriod; i++) {
            this.energySum.add((double) 0);
            this.energySumCarre.add((double) 0);
        }
        this.maxCommFrequency = maxCommFrequency;
        this.batterySize = DEFAULT_BATTERY_SIZE;
    }

    public void receiveEnergy(double energyPower, double seconds) {
        this.energy += energyPower * seconds;
    }

    public void consumeEnergy(double seconds) {
        //System.out.println("energy before consuming : " + this.energy) ;
        this.energy -= commFrequency * COMMUNICATION_COST * seconds + idleCost * seconds;
    }

    public void updateEnergy(double energyPower, double seconds) {
        this.receiveEnergy(energyPower, seconds);
        this.consumeEnergy(seconds);

        //IF THE DEVICE RUNS OUT OF ENERGY
        if (this.energy < 0) {

            //System.out.println("Oh no energy = "+ this.energy) ;
            commFrequency = (this.energy + commFrequency * COMMUNICATION_COST * seconds) / (COMMUNICATION_COST * seconds);
            //System.out.println("new commFreq = " + commFreq) ;
            if (commFrequency < 0) commFrequency = 0;
            this.energy = 0;
        }
        this.energy = Math.min(energy, batterySize);
    }

    public double getEnergy() {
        //	System.out.println(energy) ;
        return energy;
    }

    public double getCommunicationFrequency() {
        return commFrequency;
    }

    public abstract double getInstantThroughput();

    public void addEnergyData(double energyPower, double seconds) {

        energySum.set(counterEnergy, energySum.get(counterEnergy) + energyPower);
        energySumCarre.set(counterEnergy, energySumCarre.get(counterEnergy) + (energyPower * energyPower));
        this.energyData.get(counterEnergy).add(energyPower);

        if (periodNumber >= periodMax) {
            double formerEnergy = this.energyData.get(counterEnergy).removeFirst();
            //System.out.println("Former energy = " + formerEnergy) ;
            //System.out.println("LinkedList size : " + this.energyData.get(counterEnergy).size());

            energySum.set(counterEnergy, energySum.get(counterEnergy) - formerEnergy);
            energySumCarre.set(counterEnergy, energySumCarre.get(counterEnergy) - (formerEnergy * formerEnergy));
        }
        counterEnergy++;
        if (counterEnergy >= energyPeriod) {
            counterEnergy = 0;
            periodNumber++;
            if (periodNumber > periodMax)
                periodNumber = periodMax;

            if (periodNumber > 1) {
                availableEnergy = 0;
                List<Double> sigma = new ArrayList<>();
                //System.out.println("periodNumber = " + periodNumber) ;
                for (int i = 0; i < energyPeriod; i++) {
                    double sigmai = energySumCarre.get(i) / periodNumber - (energySum.get(i) / periodNumber) * (energySum.get(i) / periodNumber);
                    if (sigmai < 0)
                        sigmai = 0;
                    sigma.add(Math.sqrt(sigmai));
                    //System.out.println("sigma = " +sigma.get(i));
                    availableEnergy += (energySum.get(i) / periodNumber - sigmai) * seconds;
                }
                //System.out.println(eDispo) ;
                //System.out.println("Energy sum 1000 = "+ this.energySum.get(1000));
                double cfMax = 1000;//Math.max(0,(this.energyMax-idleCost*seconds)/(commCost*seconds))	;
                if (mode == DeviceMode.ODMACPP_GB)
                    cfm = computeFreq(0, cfMax, 0, this.energy, sigma, 0.01, energyPeriod, seconds);
                if (mode == DeviceMode.ODMACPP_SLB)
                    cfm = Math.max(0, (availableEnergy - idleCost * seconds * energyPeriod) / (COMMUNICATION_COST * seconds * energyPeriod));
            }
        }
    }

    public double c = 0.2;
    public double a = 1d / 10d;

    @Override
    public void updateParameters() {
        switch (mode) {

            case CONSTANT_FREQUENCY:
                if (this.energy < 100) commFrequency = 0;
                else commFrequency = c;
                break;

            case PROPORTIONAL_FREQUENCY:
                commFrequency = energy * a;
                if (commFrequency < 0) commFrequency = 0;
                break;

            // MODE 2 : FREQUENCE MOYENNE UTILISEE POUR NE PAS AVOIR DE TEMPS MORT : ODMAC++
            case ODMACPP_GB:
            case ODMACPP_SLB:
                if (this.energy == 0) commFrequency = 0;
                if (this.counterEnergy == 0) commFrequency = cfm;
                break;
        }

        commFrequency = Math.min(commFrequency, maxCommFrequency);
    }

    private double computeFreq(double fmin, double fmax, double lastfpos, double E0, List<Double> sigma, double acc, double ep, double seconds) {
        double f = (fmin + fmax) / 2;
        //System.out.println("f = " + f) ;
        double Eprev = E0;
        for (int i = 0; i < ep; i++) {
//			System.out.println("Em = " + (energySum.get(i)/periodNumber));
//					System.out.println("sigma = " +sigma.get(i)) ;

            Eprev += Math.min((energySum.get(i) / periodNumber - sigma.get(i) - (idleCost + f * COMMUNICATION_COST)) * seconds, this.batterySize - Eprev);
            if (Eprev < 0) {
                if (fmax - fmin < acc) {
                    //System.out.println("f final : " + lastfpos) ;
                    return lastfpos;
                } else {
                    //System.out.println("Eprev = " + Eprev) ;
                    return computeFreq(fmin, f, lastfpos, E0, sigma, acc, ep, seconds);
                }
            }
        }

        if (fmax - fmin < acc)
            return f * 0.92;
        else {
            if (Eprev < Math.min(this.batterySize / 1.8, E0))
                return computeFreq(fmin, f, f, E0, sigma, acc, ep, seconds);
            else
                return computeFreq(f, fmax, f, E0, sigma, acc, ep, seconds);
        }
    }
}
