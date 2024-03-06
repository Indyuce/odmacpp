package model;

class SimulatedSink extends SimulatedDevice {

	Sink sink ;
	protected SimulatedSink(Sink s,double spf, int tStart, int tEnd)
	{
		super(spf, tStart, tEnd) ;
		this.sink = s ;
		
	}
	@Override
	protected Device getDev() {
		return sink ;
	}
	
	public void stepSimul(int i , double energyAvailable)
	{
		super.stepSimul(i, energyAvailable);
	//	System.out.println("Throughput = " + getDev().getInstantThroughput()) ;

	}
	
	
}
