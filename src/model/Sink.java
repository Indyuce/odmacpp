package model;

public class Sink extends Device {

	public int mode ;
	public double c = 0.2 ;
	public double a = 1d/10d ;
	public Sink (int ep)
	{
		super(ep) ;
		System.out.println(a) ;

	}

	@Override
	public double getInstantThroughput() {
		
//		System.out.println("Energy = "+ energy) ;
		//System.out.println("CommFreq = " + commFreq) ;
			return this.commFreq ;
	}

	@Override
	public SimulatedDevice createSimulated(double spf, int tStart, int tEnd) {
		return new SimulatedSink(this,spf, tStart , tEnd ) ;
	}

	@Override
	public void updateParameters() {
		switch (mode)
		{
	// MODE 0 : CONSOMMATION D'ENERGIE CONSTANTE
		case 0:	
			if(this.energy < 100)
				commFreq = 0 ;
			else
				commFreq = c ;
			break ;
	// MODE 1 : FREQUENCE D'ENVOI PROPORTIONNELLE A l'ENERGIE STOCKEE
		case 1:
			commFreq = energy*a;	
			if(commFreq < 0)
				commFreq = 0 ;
			break ;
	// MODE 2 : FREQUENCE MOYENNE UTILISEE POUR NE PAS AVOIR DE TEMPS MORT : ODMAC++
		case 2:
			if(this.energy == 0)
				commFreq = 0 ;
			if(this.counterEnergy == 0)
				commFreq = cfm ;
			break ;
		}
		if(commFreq > commFreqMax)
			commFreq = commFreqMax ;

	}
	
	
	
	
	


}
