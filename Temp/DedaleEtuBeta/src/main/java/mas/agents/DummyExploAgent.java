package mas.agents;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import env.Environment;
import mas.abstractAgent;
import mas.behaviours.*;
import mas.structuregraph.Castle;
import mas.utility.*;
import jade.core.AID;
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
	private Castle castle;
	private int when= 0;
	//private boolean done = false;



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
		HashMap<AID,String> agents_position = new HashMap<AID,String>();
		HashMap<AID,Integer> agents_position_probability = new HashMap<AID,Integer>();
		this.castle = new Castle(this,agents_position,agents_position_probability);
		//this.graph.display();
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
		HashSet<AID> lsend = new HashSet<AID>();
		HashSet<AID> lack = new HashSet<AID>();
		HashMap<AID,Integer> hmsend = new HashMap<AID,Integer>();
		HashMap<AID,Integer> hmack = new HashMap<AID,Integer>();
		addBehaviour(new SayThereGraphBehaviour(this, result));
		addBehaviour(new ReceiveThereBehaviour(this,lsend,agents_position,agents_position_probability));
		addBehaviour(new GiveGraphBehaviour(this,castle, lsend,hmsend,hmack));
		addBehaviour(new ReceiveGraphBehaviour(this,castle,lack));
		addBehaviour(new SayAckGraphBehaviour(this,lack));
		addBehaviour(new ReceiveAckBehaviour(this,hmsend,hmack,agents_position,agents_position_probability));
		addBehaviour(new RandomWalkBehaviour(this,castle));

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}
	
	public void incWhen(){
		this.when++;
	}
	
	public int getWhen(){
		return when;
	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
}
