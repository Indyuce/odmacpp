package model;

import model.energy.EnergyArrivalModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
public class Simulation {
	
	private int tStart ;
	int duration = 1 ;
	Sink sink ;
	private boolean realData = true ;
	private int tEnd ;
	private Simulated sim ;
	private double secondPerFrame ;
	private EnergyArrivalModel eModel ;
	private ArrayList<Double> energyData ;
	
	
	public Simulation(int tStart, int tEnd, Simulable sim, double secondPerFrame, EnergyArrivalModel eModel)
	{
		this.tStart = tStart ;
		this.tEnd = tEnd ;
		duration = (this.tEnd-this.tStart)/1440 ;
		this.eModel = eModel ;
		this.energyData = new ArrayList<Double>() ;
		this.sim = sim.createSimulated(secondPerFrame, tStart, tEnd) ;
		this.secondPerFrame  = secondPerFrame ;
	}
	
	public void simulate(boolean b, boolean realData, Sink s)
	{
		//INIT DATA
		sim.initData() ;
		
		this.realData = realData ;
		this.sink = s ;
		//SIMULATE FROM T_START TO T_END
		for(int j = tStart+1 ; j < tEnd ; j++)
		{
			double energyAvailable = eModel.getEnergy(j*secondPerFrame) ;
			sim.stepSimul(j-tStart, energyAvailable);
			energyData.add(energyAvailable) ;
		}
		
		if(b)
			export(); 
	}
	
	public ArrayList<ArrayList<Double>> getData()
	{
		ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>() ;
		dataSet.add(energyData) ;
		dataSet.addAll(sim.getData()) ;
		
		return dataSet ;
	}

	public double getSecondPerFrame() {
		return secondPerFrame;
	}
	
	public static double getArea(ArrayList<Double> ar)
	{
		double result = 0 ;
		for(int i = 1 ; i < ar.size() ; i++)
		{
			result  += (ar.get(i-1)+ ar.get(i))/2 ;
		}
		return result ;
	}
	
	public void export()
	{
		File f ;
		String name = "" ;
		String title = "" ;
		if(realData)
			{
				name += "rdata_" ;
				title = "Real data - " ;
			}
		else
			{
				name += "sdata_" ;
				title += "Simulated data - " ;
			}
		
		name += (duration+"d_") ;		
		name += (sink.energyMax+"b_");
		
		switch(sink.mode)
		{
		case 0 : 
			name += "const_c_eq_" + (String.format("%1.1e",sink.c));
			title += "Constant, c = " +(String.format("%1.1e",sink.c));
			break ;
		case 1 :
			name += "prop_a_eq_" + (String.format("%1.1e",sink.a));
			title += "Proportional, alpha = " + (String.format("%1.1e",sink.a)) ;
			break ;
		case 2 : 
			if(sink.version == 0)
			{
				name += "odmacpp_v0" ;
				title += "ODMAC++V0" ;		
			}
			if(sink.version == 1)
			{
				name += "odmacpp" ;
				title += "ODMAC++" ;	
			}
			
			name+= "_" + sink.periodMax + "sw"; 
			break ;
		}

			f = new File(name + ".csv") ;

		
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8) ) ;

			pw.println(title) ;
			pw.println("0;1;2;3") ;
			pw.println("Time (days);Energy Profile (W/m2);Battery Level (J);Throughput (Packets/s)") ;
			double t = tStart ;
			ArrayList<ArrayList<Double>> data = sim.getData() ;
			
			for(int j = 0 ; j < energyData.size() ; j++)
			{
				
				pw.println(t+";"+energyData.get(j)+";"+data.get(0).get(j)+";" +data.get(2).get(j)) ;
				t += secondPerFrame/86400 ;
			}
			
			pw.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	
}
