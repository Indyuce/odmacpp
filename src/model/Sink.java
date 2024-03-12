package model;

import simulation.SimulatedSink;
import simulation.Simulation;

public class Sink extends Device {
    public final int mode;
    public double c = 0.2;
    public double a = 1d / 10d;

    public Sink(int ep, int mode, double commFreqMax) {
        super(ep, commFreqMax);

        this.mode = mode;
    }

    @Override
    public double getInstantThroughput() {
        // No collision possible as there is only one downlink sink channel
        return this.commFrequency;
    }

    @Override
    public SimulatedSink createSimulated(Simulation simulation) {
        return new SimulatedSink(simulation, this);
    }

    @Override
    public void updateParameters() {
        switch (mode) {
            // MODE 0 : CONSOMMATION D'ENERGIE CONSTANTE
            case 0:
                if (this.energy < 100) commFrequency = 0;
                else commFrequency = c;
                break;
            // MODE 1 : FREQUENCE D'ENVOI PROPORTIONNELLE A l'ENERGIE STOCKEE
            case 1:
                commFrequency = energy * a;
                if (commFrequency < 0) commFrequency = 0;
                break;
            // MODE 2 : FREQUENCE MOYENNE UTILISEE POUR NE PAS AVOIR DE TEMPS MORT : ODMAC++
            case 2:
                if (this.energy == 0) commFrequency = 0;
                if (this.counterEnergy == 0) commFrequency = cfm;
                break;
        }

        commFrequency = Math.min(commFrequency, maxCommFrequency);
    }
}
