package mas.behaviours;


import jade.core.ContainerID;
import jade.core.behaviours.TickerBehaviour;

public class MigrateBehaviour extends TickerBehaviour {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public MigrateBehaviour (final mas.abstractAgent myagent) {
		super(myagent, 3000);
	}

	
	protected void onTick() {
		
		ContainerID cID = new ContainerID();
		cID.setName("MyDistantContainer0");
		cID.setPort("Lgk");
		cID.setAddress("132.227.113.238");
		
		this.myAgent.doMove(cID);
		
	}

}
