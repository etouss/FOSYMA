package mas.agents;


import java.util.HashSet;
import java.util.LinkedList;

import env.Environment;
import mas.abstractAgent;
import mas.behaviours.*;
import mas.utility.*;
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
	private boolean done = false;



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
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd );
		} catch (FIPAException fe) 
			{
			fe.printStackTrace(); 
		}
		

		
		//Add the behaviours
		this.graph = new MyGraph(this);
		this.graph.display();
		//addBehaviour(new SayHello(this));
		//sd.setType( "explorer" ); 
		//dfd.addServices(sd);
		
		ServiceDescription sdsearch = new ServiceDescription();
		sdsearch.setType( "explorer" ); 
		DFAgentDescription dfdsearch = new DFAgentDescription();
		dfdsearch.addServices(sdsearch);
		
		DFAgentDescription[] result = null;
		try {
			Thread.sleep(1000);
			result = DFService.search(this, dfdsearch);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result.length + " results" );
		if (result.length>0){
			for(DFAgentDescription a : result){
				System.out.println(" " + a.getName()+ "\n" );
			}
		}
		HashSet<TupleAidTick> ll = new HashSet<TupleAidTick>();
		addBehaviour(new SayThereGraphBehaviour(this, result));
		addBehaviour(new ReceiveThereBehaviour(this,ll));
		addBehaviour(new GiveGraphBehaviour(this,graph, ll));
		addBehaviour(new ReceiveGraphBehaviour(this,graph));
		addBehaviour(new RandomWalkBehaviour(this,graph));

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
