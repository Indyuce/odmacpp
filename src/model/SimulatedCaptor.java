package model;

class SimulatedCaptor extends SimulatedDevice{

private Captor captor ;

protected SimulatedCaptor(Captor c, double spf, int tStart, int tEnd)
{
	super(spf, tStart, tEnd);
	this.captor = c ;
}

	@Override
	protected Device getDev() {
		return captor ;
	}

public Captor getCaptor() {
	return captor;
}
	
}
