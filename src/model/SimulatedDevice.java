package model;

import java.util.ArrayList;

abstract class SimulatedDevice extends Simulated{

//	private int tEnd ;
//	private int tStart ;
	protected ArrayList<Double> energyRecord ;
	protected ArrayList<Double> freqRecord ;
	protected ArrayList<Double> throughputRecord ;
	protected ArrayList<ArrayList<Double>> dataSet ;
	
	
	protected SimulatedDevice(double spf, int tStart, int tEnd)
	{
		super(spf) ;
		this.energyRecord = new ArrayList<Double>(tEnd-tStart) ;
		
		this.freqRecord = new ArrayList<Double>(tEnd-tStart) ;
		this.throughputRecord = new ArrayList<Double>(tEnd-tStart) ;
		this.dataSet = new ArrayList<ArrayList<Double>>(3) ;
		dataSet.add(energyRecord) ;
		dataSet.add(freqRecord) ;
		dataSet.add(throughputRecord);
	}
	
	public void stepSimul(int i, double energyAvailable)
	{	
		this.getDev().addEnergyData(energyAvailable, this.secondPerFrame);
		this.getDev().updateParameters();
		this.getDev().updateEnergy(energyAvailable, this.secondPerFrame) ;
		
		//TODO ADD THIS.SECONDPERFRAME
	//	System.out.println("Energy = " +getDev().getEnergy()) ;
		energyRecord.add(i,this.getDev().getEnergy()) ;
		throughputRecord.add(i,getDev().getInstantThroughput()*secondPerFrame) ;

		


	//	freqRecord.add(i,getDev().getInstantThroughput()) ;

	}
	
	protected abstract Device getDev() ;
	
	public void initData()
	{
		this.energyRecord.add(getDev().getEnergy()) ;
		this.freqRecord.add(getDev().getFreq()) ;
		this.throughputRecord.add(getDev().getInstantThroughput()) ;
	}
	
	public ArrayList<ArrayList<Double>> getData()
	{
		return dataSet ;
	}
	
	
	

	
	
}
