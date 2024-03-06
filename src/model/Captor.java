package model;

public class Captor extends Device {

    protected double pc = 1; //collision probability

    public Captor(double pc, int ep) {
        super(ep);
        this.pc = pc;
    }

    @Override
    public double getInstantThroughput() {
        return this.commFreq * pc;
    }

    @Override
    public SimulatedDevice createSimulated(double spf, int tStart, int tEnd) {
        return new SimulatedCaptor(this, spf, tStart, tEnd);
    }

    @Override
    public void updateParameters() {
    }


}
