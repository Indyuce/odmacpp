import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import graphics.GraphFrame;
import model.energy.EnergyArrivalModel;
import model.Cluster;
import model.Simulation;

public class Main {
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double secondPerFrame = 60 ;
		boolean realData = true ;
		double secondPerPeriod = 86400/secondPerFrame ;
		System.out.println(secondPerPeriod) ;
		

		
		boolean memo = false ;
		//generateAllData(memo,40000, secondPerPeriod, secondPerFrame) ;
		//generateAllData(memo, 10000, secondPerPeriod, secondPerFrame) ;
		simDataODMACPP(memo, true, 90 ,20000,secondPerPeriod, secondPerFrame,0,90) ;
		simDataODMACPP(memo, true, 90 ,20000,secondPerPeriod, secondPerFrame,1,90) ;
	//	generateData(memo, true, 7 ,40000,secondPerPeriod, secondPerFrame) ;
//		simData(memo, false, 30 ,40000,secondPerPeriod, secondPerFrame,2) ;
//		generateDataPaper(memo, secondPerPeriod, secondPerFrame);
		//@SuppressWarnings("unused")
		//GraphFrame frame = new GraphFrame(1024,768, simul);
	}
	
	public static void generateDataPaper(boolean memo, double secondPerPeriod, double secondPerFrame)
	{
		simDataConst(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,0.5) ;
		simDataConst(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,5) ;
		simDataConst(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,20) ;

		simDataProp(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,1d/100000d) ;
		simDataProp(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,1d/5000d) ;
		simDataProp(memo, false, 7 ,40000,secondPerPeriod, secondPerFrame,1d) ;
		
		simDataODMACPP(memo, false, 30 ,40000,secondPerPeriod, secondPerFrame,0,90) ;
		simDataODMACPP(memo, false, 30 ,40000,secondPerPeriod, secondPerFrame,1,90) ;
		
		simDataProp(memo, true, 30, 40000, secondPerPeriod, secondPerFrame, 1d/5000d) ;
		
		simDataODMACPP(memo, true, 30 ,40000,secondPerPeriod, secondPerFrame,0,90) ;
		simDataODMACPP(memo, true, 30 ,40000,secondPerPeriod, secondPerFrame,1,90) ;

		simDataODMACPP(memo, true, 30 ,10000,secondPerPeriod, secondPerFrame,0,90) ;
		simDataODMACPP(memo, true, 30 ,10000,secondPerPeriod, secondPerFrame,1,90) ;
		
		simDataODMACPP(memo, true, 100 ,40000,secondPerPeriod, secondPerFrame,1,100) ;
		simDataODMACPP(memo, true, 100 ,40000,secondPerPeriod, secondPerFrame,1,15) ;
	}
	
	public static void generateAllData(boolean memo, double maxBattery, double secondPerPeriod, double secondPerFrame)
	{
//		generateData(memo, false, 7 ,maxBattery,secondPerPeriod, secondPerFrame) ;
//		generateData(memo, true, 7 ,maxBattery,secondPerPeriod, secondPerFrame) ;
//		
//		generateData(memo, false, 30 ,maxBattery,secondPerPeriod, secondPerFrame) ;
//		generateData(memo, true, 30 ,maxBattery,secondPerPeriod, secondPerFrame) ;
		
//		generateData(memo, false, 90 ,maxBattery,secondPerPeriod, secondPerFrame) ;
		generateData(memo, true, 30 ,maxBattery,secondPerPeriod, secondPerFrame) ;
	}
	
	
	public static void generateData(boolean memo, boolean realData, int duration,double maxBattery, double secondPerPeriod, double secondPerFrame)
	{
		
		EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame);
		
		
		for(int i = 0 ; i < 3 ; i++)
		{
			if(i != 2)
			{					
				Cluster h = new Cluster(5, eModel.getEnergyPeriod(),i ) ;
				h.getSink().setEnergyMax(maxBattery);
				double a = h.getSink().a ;
				double c = h.getSink().c ;
				for(int j = 0 ; j < 3 ; j++)
				{
					Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
					if(!memo)
					{
					simul.simulate(false, realData, h.getSink());
					GraphFrame frame = new GraphFrame(1024,768, simul);
					}
					else
					{
						simul.simulate(true, realData, h.getSink());
					}	
					h.getSink().a /= 2 ;
					h.getSink().c /= 4 ;
				}
				h.getSink().a = a ;
				h.getSink().c = c ;
				
			}
			else
			{
			{
				Cluster h = new Cluster(5, eModel.getEnergyPeriod(),i ) ;
				h.getSink().setEnergyMax(maxBattery);
				h.getSink().version = 0 ;
				Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
					if(!memo)
					{
					simul.simulate(false, realData, h.getSink());
					GraphFrame frame = new GraphFrame(1024,768, simul);
					}
					else
					{
						simul.simulate(true, realData, h.getSink());
					}
			}
			}
			{	
				Cluster h = new Cluster(5, eModel.getEnergyPeriod(),i ) ;
				h.getSink().setEnergyMax(maxBattery);
				h.getSink().version = 1 ;
				Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
					if(!memo)
					{
					simul.simulate(false, realData, h.getSink());
					GraphFrame frame = new GraphFrame(1024,768, simul);
					}
					else
					{
						simul.simulate(true, realData, h.getSink());
					}
			}
		}
	}
	
	public static void simDataProp(boolean memo, boolean realData, int duration,double maxBattery, double secondPerPeriod, double secondPerFrame, double a)
	{
		EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame) ;
		Cluster h = new Cluster(5, eModel.getEnergyPeriod(),1 ) ;
		h.getSink().setEnergyMax(maxBattery);
		h.getSink().a =a ;
		Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
			if(!memo)
			{
			simul.simulate(false, realData, h.getSink());
			GraphFrame frame = new GraphFrame(1024,768, simul);
			}
			else
			{
				simul.simulate(true, realData, h.getSink());
			}
		
	}
	
	public static void simDataConst(boolean memo, boolean realData, int duration,double maxBattery, double secondPerPeriod, double secondPerFrame, double c)
	{
		EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame) ;
		Cluster h = new Cluster(5, eModel.getEnergyPeriod(),0 ) ;
		h.getSink().setEnergyMax(maxBattery);
		h.getSink().c =c ;
		Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
			if(!memo)
			{
			simul.simulate(false, realData, h.getSink());
			GraphFrame frame = new GraphFrame(1024,768, simul);
			}
			else
			{
				simul.simulate(true, realData, h.getSink());
			}
	}
	public static void simDataODMACPP(boolean memo, boolean realData, int duration,double maxBattery, double secondPerPeriod, double secondPerFrame, int version, int SW)
	{
		EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame) ;
		Cluster h = new Cluster(5, eModel.getEnergyPeriod(),2 ) ;
		h.getSink().setEnergyMax(maxBattery);
		h.getSink().version = version ;
		h.getSink().periodMax = SW ;
		Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
			if(!memo)
			{
			simul.simulate(false, realData, h.getSink());
			GraphFrame frame = new GraphFrame(1024,768, simul);
			}
			else
			{
				simul.simulate(true, realData, h.getSink());
			}
	}
	
	
	public static void simData(boolean memo, boolean realData, int duration,double maxBattery, double secondPerPeriod, double secondPerFrame, int mode)
	{
		EnergyArrivalModel eModel = generateModel(realData, secondPerPeriod, secondPerFrame) ;
		Cluster h = new Cluster(5, eModel.getEnergyPeriod(),mode ) ;
		h.getSink().setEnergyMax(maxBattery);
		h.getSink().version = 0 ;
		Simulation simul = new Simulation(0, 60*24*duration, h,secondPerFrame, eModel) ;
			if(!memo)
			{
			simul.simulate(false, realData, h.getSink());
			GraphFrame frame = new GraphFrame(1024,768, simul);
			}
			else
			{
				simul.simulate(true, realData, h.getSink());
			}
	}
	
	public static EnergyArrivalModel generateModel(boolean realData, double secondPerPeriod, double secondPerFrame)
	{
		
		EnergyArrivalModel eModel ;
		if(!realData)
		{
		eModel	= new EnergyArrivalModel(){
			Random r = new Random() ;
			double currGauss = r.nextGaussian()/10 ;
			int counter = 0 ;
			@Override
			public double getEnergy(double t) {
				//System.out.println(t) ;
				counter ++; 
				if(counter == 60)
				{
					currGauss = r.nextGaussian()/4 ; 
					counter = 0 ;
				}
				return Math.max(0, Math.sin(t*2*Math.PI/(secondPerPeriod*secondPerFrame))+ currGauss) ;
			}
			@Override
			public int getEnergyPeriod() {
				return (int)secondPerPeriod;
			}
		};
		}
		else
		{
			eModel = new EnergyArrivalModel(){			
			double[][] data = new double[2][40000] ;
			{
				try {
					BufferedReader r = new BufferedReader(new FileReader("data/data_mississipi.csv"));
					String s ;
					int i = 0 ;
					while ((s= r.readLine()) != null)
							{
								String[] table = s.split(";");
								data[0][i] = Double.parseDouble(table[0]) ;
								data[1][i] = Math.max(0, Double.parseDouble(table[1])) ;
								i++;
							}
					r.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			public double getEnergy(double t) {
				return data[1][(int) (t/(5*60))]*0.2/(100);
			}		
			@Override
			public int getEnergyPeriod() {
				return (int)secondPerPeriod;
			}
		} ;
		}
		return eModel ;
	}

}
