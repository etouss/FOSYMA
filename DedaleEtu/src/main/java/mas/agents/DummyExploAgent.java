package mas.agents;


import env.Environment;

import mas.abstractAgent;
import mas.behaviours.*;
import mas.structuregraph.MyGraph;


public class DummyExploAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;
	private MyGraph graph;
	private int timestamp= 0;



	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}

		//Add the behaviours
		this.graph = new MyGraph(this);
		this.graph.display();
		addBehaviour(new RandomWalkBehaviour(this,graph));
		//addBehaviour(new SayHello(this));
		addBehaviour(new GiveGraphBehaviour(this,graph));
		addBehaviour(new ReceiveGraphBehaviour(this,graph));

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}
	
	public void ticking(){
		this.timestamp++;
	}
	
	public int ticked(){
		return timestamp;
	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
}
