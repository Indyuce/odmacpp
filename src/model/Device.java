package model;

import model.energy.EnergyUser;

import java.util.ArrayList;
import java.util.LinkedList;

abstract class Device implements EnergyUser, Simulable {

    protected double energyMax = 1000;
    protected double commFreqMax = 500;
    protected double commCost = 0.1;
    protected double idleCost = 0.1;
    public int version = 1;
    protected int ep;
    protected ArrayList<Double> energySum;
    protected ArrayList<LinkedList<Double>> energyData;
    protected ArrayList<Double> energySumCarre;
    protected int eDispo = 0;
    protected int eLost = 0;
    protected int eLostSum = 0;
    protected int counterEnergy = 0;
    protected double periodNumber = 0;
    public int periodMax = 90;
    protected double cfm = 0;

    public final static double ENERGY_INIT = 3;
    public final static double COMM_FREQ_INIT = 0;

    protected double energy = ENERGY_INIT;
    //protected double energyEff = E
    protected double commFreq = COMM_FREQ_INIT;


    public Device(int ep) {
        this.ep = ep;
        this.energySum = new ArrayList<Double>(ep);
        this.energySumCarre = new ArrayList<Double>(ep);
        this.energyData = new ArrayList<LinkedList<Double>>();
        for (int i = 0; i < ep; i++) {
            this.energyData.add(new LinkedList<Double>());
        }
        for (int i = 0; i < ep; i++) {
            this.energySum.add((double) 0);
            this.energySumCarre.add((double) 0);
        }
    }

    public void receiveEnergy(double energyPower, double seconds) {
        this.energy += energyPower * seconds;
    }

    public void consumeEnergy(double seconds) {
        //System.out.println("energy before consuming : " + this.energy) ;
        this.energy -= commFreq * commCost * seconds + idleCost * seconds;
    }

    public void updateEnergy(double energyPower, double seconds) {
        this.receiveEnergy(energyPower, seconds);
        this.consumeEnergy(seconds);

        //IF THE DEVICE RUNS OUT OF ENERGY
        if (this.energy < 0) {

            //System.out.println("Oh no energy = "+ this.energy) ;
            commFreq = (this.energy + commFreq * commCost * seconds) / (commCost * seconds);
            //System.out.println("new commFreq = " + commFreq) ;
            if (commFreq < 0)
                commFreq = 0;
            this.energy = 0;
        }
        if (this.energy > this.energyMax) {
            this.energy = this.energyMax;
        }
    }


    public double getEnergy() {
        //	System.out.println(energy) ;
        return energy;
    }

    public double getFreq() {
        return commFreq;
    }

    public abstract double getInstantThroughput();

    public void setCommFreqMax(double commFreqMax) {
        this.commFreqMax = commFreqMax;
    }

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
        if (counterEnergy >= ep) {
            counterEnergy = 0;
            periodNumber++;
            if (periodNumber > periodMax)
                periodNumber = periodMax;

            if (periodNumber > 1) {
                eDispo = 0;
                ArrayList<Double> sigma = new ArrayList<Double>();
                //System.out.println("periodNumber = " + periodNumber) ;
                for (int i = 0; i < ep; i++) {
                    double sigmai = energySumCarre.get(i) / periodNumber - (energySum.get(i) / periodNumber) * (energySum.get(i) / periodNumber);
                    if (sigmai < 0)
                        sigmai = 0;
                    sigma.add(Math.sqrt(sigmai));
                    //System.out.println("sigma = " +sigma.get(i));
                    eDispo += (energySum.get(i) / periodNumber - sigmai) * seconds;
                }
                //System.out.println(eDispo) ;
                //System.out.println("Energy sum 1000 = "+ this.energySum.get(1000));
                double cfMax = 1000;//Math.max(0,(this.energyMax-idleCost*seconds)/(commCost*seconds))	;
                if (version == 1)
                    cfm = computeFreq(0, cfMax, 0, this.energy, sigma, 0.01, ep, seconds);
                if (version == 0)
                    cfm = Math.max(0, (eDispo - idleCost * seconds * ep) / (commCost * seconds * ep));
            }
        }
    }


    private double computeFreq(double fmin, double fmax, double lastfpos, double E0, ArrayList<Double> sigma, double acc, double ep, double seconds) {
        double f = (fmin + fmax) / 2;
        //System.out.println("f = " + f) ;
        double Eprev = E0;
        for (int i = 0; i < ep; i++) {
//			System.out.println("Em = " + (energySum.get(i)/periodNumber));
//					System.out.println("sigma = " +sigma.get(i)) ;

            Eprev += Math.min((energySum.get(i) / periodNumber - sigma.get(i) - (this.idleCost + f * this.commCost)) * seconds, this.energyMax - Eprev);
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
            if (Eprev < Math.min(this.energyMax / 1.8, E0))
                return computeFreq(fmin, f, f, E0, sigma, acc, ep, seconds);
            else
                return computeFreq(f, fmax, f, E0, sigma, acc, ep, seconds);
        }

    }

    public void setEnergyMax(double energyMax) {
        this.energyMax = energyMax;
    }


}
