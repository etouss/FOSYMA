package mas.agents;


import env.Environment;
import mas.abstractAgent;
import mas.behaviours.*;
import mas.structuregraph.MyGraph;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;;

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
		
		
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription();
		sd.setType( "explorer" ); 
		sd.setName(getLocalName() );
		dfd.addServices(sd);
		try {
		DFService.register(this, dfd );
		} catch (FIPAException fe) { fe.printStackTrace(); }
		

		
		//Add the behaviours
		this.graph = new MyGraph(this);
		this.graph.display();
		addBehaviour(new RandomWalkBehaviour(this,graph));
		//addBehaviour(new SayHello(this));
		sd.setType( "explorer" ); 
		dfd.addServices(sd);
		DFAgentDescription[] result = null;
		try {
			result = DFService.search(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result.length + " results" );
		if (result.length>0)
		System.out.println(" " + result[0].getName() );
		
		
		addBehaviour(new GiveGraphBehaviour(this,graph, result));
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
