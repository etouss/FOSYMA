package mas.behaviours;

import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import mas.agents.*;


public class SayAckGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	//private MyGraph graph;
	private HashSet<AID> lack;
	
	
	public SayAckGraphBehaviour (final Agent myagent, HashSet<AID> lack) {
		super(myagent, 500);
		//this.graph = graph;
		this.lack = lack;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		
		for(AID aid : lack ){
			String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
			ACLMessage msg=new ACLMessage(ACLMessage.CONFIRM);
			msg.setSender(this.myAgent.getAID());
			msg.setContent("AckGraph! ::"+myPosition);
		
			msg.addReceiver(aid);
			((mas.abstractAgent)this.myAgent).sendMessage(msg);
			lack.remove(aid);
			break;
		}	
	}
}