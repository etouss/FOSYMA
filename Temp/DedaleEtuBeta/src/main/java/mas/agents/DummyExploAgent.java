package mas.agents;


import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashMap;
import java.util.HashSet;

import mas.abstractAgent;
import mas.behaviour.Walk;
import mas.behavioursCom.ReceiveConfirm;
import mas.behavioursCom.ReceiveInform;
import mas.behavioursCom.ReceiveRequest;
import mas.behavioursCom.SayConfirm;
import mas.behavioursCom.SayInform;
import mas.behavioursCom.SayRequest;
import mas.data.AgentInfo;
import mas.data.Meeting;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import env.Environment;

public class DummyExploAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;
	private int when= 0;
	private Castle castle;
	private Meeting meeting;
	//private AgentInfo me;
	private AgentDescisionMaker dm;
	

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
		//this.agents = new HashMap<AID,AgentInfo>();
		
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
		/*
		System.out.println(result.length + " results" );
		if (result.length>0){
			for(DFAgentDescription a : result){
				System.out.println(" " + a.getName()+ "\n" );
			}
		}
		*/
		HashSet<AID> lsend = new HashSet<AID>();
		HashSet<AID> lack = new HashSet<AID>();
		HashMap<AID,Integer> hmsend = new HashMap<AID,Integer>();
		HashMap<AID,Integer> hmack = new HashMap<AID,Integer>();
		HashMap<AID,Long> hmcode = new HashMap<AID,Long>();
		AgentLock un_move = new AgentLock(this.getAID());
		
		
		addBehaviour(new SayRequest(this, result,un_move));
		addBehaviour(new ReceiveRequest(this,hmack,un_move));
		addBehaviour(new SayInform(this, lsend,hmsend,hmack,hmcode,un_move));
		addBehaviour(new ReceiveInform(this,lack,un_move));
		addBehaviour(new SayConfirm(this,lack,un_move));
		addBehaviour(new ReceiveConfirm(this,lsend,hmsend,hmack,hmcode,un_move));
		addBehaviour(new Walk(this,lsend,lack,un_move));

		System.out.println("the agent "+this.getLocalName()+ " is started");
		//totalbackpack = this.getBackPackFreeSpace();
		
		this.castle = new Castle(this);
		this.meeting = new Meeting(this,result.length);
		this.dm = new AgentDescisionMaker(meeting,castle);
		
		
		//data = new Data(this,result.length);
		//dm = new AgentDescisionMaker(this);

	}
	
	public void incWhen(){
		this.when++;
		meeting.update_me(this);
	}
	
	public int getWhen(){
		//System.out.println(when);
		return when;
	}
	
	public Castle getCastle(){
		return this.castle;
	}
	public Meeting getMeeting(){
		return this.meeting;
	}
	public AgentDescisionMaker getDM(){
		return this.dm;
	}
	
	
	protected void takeDown(){

	}
}
