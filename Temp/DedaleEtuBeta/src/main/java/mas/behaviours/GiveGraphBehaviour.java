package mas.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;



public class GiveGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private Castle castle;
	private HashSet<AID> lsend;
	private HashMap<AID,Integer> hmsend;
	private HashMap<AID,Integer> hmack;
	
	public GiveGraphBehaviour (final Agent myagent,Castle castle, HashSet<AID> lsend,HashMap<AID,Integer> hmsend,HashMap<AID,Integer> hmack) {
		super(myagent, 500);
		this.castle = castle;
		this.lsend = lsend;
		this.hmsend = hmsend;
		this.hmack = hmack;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		for(AID aid : lsend ){
			ACLMessage msg=new ACLMessage(7);
			msg.setSender(this.myAgent.getAID());
	
			
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to send graph to its friends: "+graph.toString());
			try {
				if(hmack.containsKey(aid)){
					msg.setContentObject(castle.room_to_send(hmack.get(aid)));
					//
				}
				else{
					msg.setContentObject(castle.room_to_send(0));
				}
				hmsend.remove(aid);
				hmsend.put(aid, ((DummyExploAgent)myAgent).getWhen());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(aid);
			((mas.abstractAgent)this.myAgent).sendMessage(msg);
			lsend.remove(aid);
			break;
		}
	}

}