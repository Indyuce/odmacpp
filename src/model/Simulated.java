package model;

import java.util.ArrayList;

abstract class Simulated {

	protected double secondPerFrame ;
	
	public Simulated(double spf)
	{
		this.secondPerFrame = spf ;
	}
	
	public abstract void initData() ;
	
	public abstract void stepSimul(int i, double energyAvailable) ;
	
	public abstract ArrayList<ArrayList<Double>> getData() ;
	
	
}
