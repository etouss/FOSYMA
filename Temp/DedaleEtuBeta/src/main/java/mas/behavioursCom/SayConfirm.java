package mas.behavioursCom;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import mas.agents.*;
import mas.com.Confirm;
import mas.com.DataConfirm;
import mas.structuregraph.Castle;


public class SayConfirm extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	//private MyGraph graph;
	private HashSet<AID> lack;
	private Castle castle;
	private AgentLock un_move;
	private DummyExploAgent agent;
	
	
	public SayConfirm (final DummyExploAgent myagent, HashSet<AID> lack,Castle castle,AgentLock un_move) {
		super(myagent, 500);
		//this.graph = graph;
		this.lack = lack;
		this.castle = castle;
		this.un_move = un_move;
		this.agent = myagent;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		//int when = ((DummyExploAgent)this.myAgent).getWhen();
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		ACLMessage msg=new ACLMessage(ACLMessage.CONFIRM);
		msg.setSender(this.myAgent.getAID());
		try {
			msg.setContentObject(new DataConfirm(this.agent,Confirm.Graph,castle.hash_castle()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//msg.setContent("AckGraph! ::"+myPosition+"::"+castle.hash_castle());
		for(AID aid : lack ){
			msg.addReceiver(aid);
			un_move.unset_lock_move(aid);
			//lack.remove(aid);
			//break;
		}
		lack.removeAll(lack);
		((mas.abstractAgent)this.myAgent).sendMessage(msg);
	}
}