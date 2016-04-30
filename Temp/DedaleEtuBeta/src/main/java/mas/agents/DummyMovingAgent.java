package mas.agents;

import mas.abstractAgent;

public class DummyMovingAgent extends abstractAgent {
	
	private static final long serialVersionUID = -5686331366676803589L;
	
	protected void setup(){
		super.setup();
	}
	
	protected void beforeMove(){
		super.beforeMove();
		System.out.println("I migrate");
	}
	
	protected void afterMove(){
		super.afterMove();
		System.out.println("I migrated");
	}
}
