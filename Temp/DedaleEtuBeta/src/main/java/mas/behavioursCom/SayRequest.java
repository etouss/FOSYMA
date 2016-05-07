package mas.behavioursCom;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashSet;
import mas.agents.*;
import mas.com.DataRequest;
import mas.com.Request;
import mas.structuregraph.Castle;


public class SayRequest extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	//private MyGraph graph;
	private DFAgentDescription[] listeAgents;
	private Castle castle;
	private AgentLock un_move;
	private DummyExploAgent agent;
	
	public SayRequest (final DummyExploAgent myagent, DFAgentDescription[] listeAgents,Castle castle,AgentLock un_move) {
		super(myagent, 500);
		this.agent = myagent;
		//this.graph = graph;
		this.listeAgents = listeAgents;
		this.castle = castle;
		this.un_move =un_move;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.myAgent.getAID());
		/*
		if(!castle.is_done_visited()){
			msg.setContent("GiveGraph! ::"+myPosition);
		}
		else{
			msg.setContent("There! ::"+myPosition);
		}
		*/
		try {
			msg.setContentObject(new DataRequest(this.agent,Request.Graph));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Evite de parler avec quelqu'un si je suis déja en conversation avec*/
		HashSet<AID> not_send = un_move.locked_move();
		
		for ( DFAgentDescription agent : listeAgents ){
			if (!agent.getName().equals(this.myAgent.getAID()) && !not_send.contains(agent.getName()))
				msg.addReceiver(agent.getName());
		}
		((mas.abstractAgent)this.myAgent).sendMessage(msg);

		

	}

}