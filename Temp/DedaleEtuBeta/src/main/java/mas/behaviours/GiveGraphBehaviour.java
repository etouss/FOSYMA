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

import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class GiveGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private Castle castle;
	private HashSet<AID> lsend;
	private HashMap<AID,Integer> hmsend;
	private HashMap<AID,Integer> hmack;
	private HashMap<AID,Long> hmcode;
	private AgentLock un_move;
	
	public GiveGraphBehaviour (final Agent myagent,Castle castle, HashSet<AID> lsend,HashMap<AID,Integer> hmsend,HashMap<AID,Integer> hmack,HashMap<AID,Long> hmcode,AgentLock un_move) {
		super(myagent, 500);
		this.castle = castle;
		this.lsend = lsend;
		this.hmsend = hmsend;
		this.hmack = hmack;
		this.hmcode = hmcode;
		this.un_move = un_move;
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
					HashSet<Room> to_send = castle.room_to_send(hmack.get(aid));
					if(to_send.isEmpty()){
						System.out.println("ERRRREUUUR");
					}
					Statistique.graph_envoye += 1;
					msg.setContentObject(to_send);
					//
				}
				else{
					HashSet<Room> to_send = castle.room_to_send(0);
					//if(to_send.isEmpty()) return;
					Statistique.graph_envoye += 1;
					msg.setContentObject(to_send);
				}
				hmsend.remove(aid);
				hmcode.remove(aid);
				hmsend.put(aid, ((DummyExploAgent)myAgent).getWhen());
				hmcode.put(aid, castle.hash_castle());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(aid);
			((mas.abstractAgent)this.myAgent).sendMessage(msg);
			lsend.remove(aid);
			//un_move.set_lock_move(aid);
			break;
		}
	}

}