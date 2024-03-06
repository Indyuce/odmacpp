package model;

import java.util.ArrayList;
import java.util.Collection;

public class Hub extends Cluster implements EnergyUser{

	private Sink sink ;
	private ArrayList<Captor> captors ;
	private ArrayList<Simulable> simulables ;
	final static double  PC_INIT = 1 ;
	
	public Hub (int nbCaptors, int ep, int mode)
	{
		this.sink = new Sink(ep) ;
		this.captors = new ArrayList<Captor>(nbCaptors) ;
		for(int i = 0 ; i < nbCaptors ; i++)
			this.captors.add(new Captor(PC_INIT,ep)) ;
		
		this.sink.setCommFreqMax(100*nbCaptors*PC_INIT);
		simulables = new ArrayList<Simulable>(nbCaptors +1) ;
		simulables.add(sink) ;
		sink.mode = mode ;
		simulables.addAll(captors) ;
	}
	
	public Sink getSink()
	{
		return sink ;
	}
	
	
	@Override
	public Simulated createSimulated(double spf, int tStart, int tEnd) {
		return new SimulatedCluster(this, spf,tStart,tEnd) ;
	}

	@Override
	Collection<Simulable> getSimulables() {
		return simulables ;
	}

	@Override
	public void updateEnergy(double energyPower, double seconds) {
		sink.updateEnergy(energyPower, seconds);
		for(Captor c : captors)
			c.updateEnergy(energyPower, seconds);
	}
	
	public void updateParameters()
	{
		double activeNumber = 0 ;
		for(Captor c : captors)
			if(c.getEnergy() != 0)
				activeNumber ++;
		
		for(Captor c : captors)
			c.pc = 1/activeNumber ;
	}
	
	
	

}
