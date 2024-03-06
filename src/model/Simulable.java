package model;

interface Simulable {
	
	public Simulated createSimulated(double spf, int tStart, int tEnd) ;
	
	void updateParameters() ;

}
