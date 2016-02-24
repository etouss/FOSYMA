package mas.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import mas.structuregraph.MyGraph;
import mas.utility.TupleAidTick;
import mas.agents.DummyExploAgent;



public class GiveGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private MyGraph graph;
	private HashSet<TupleAidTick> ll;
	
	public GiveGraphBehaviour (final Agent myagent,MyGraph graph, HashSet<TupleAidTick> ll) {
		super(myagent, 500);
		this.graph = graph;
		this.ll = ll;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		for(TupleAidTick tuple : ll ){
			ACLMessage msg=new ACLMessage(7);
			msg.setSender(this.myAgent.getAID());
	
			
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to send graph to its friends: "+graph.toString());
			try {
				msg.setContentObject(graph.ticked_send(tuple.tick));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(tuple.aid);
			((mas.abstractAgent)this.myAgent).sendMessage(msg);
			ll.remove(tuple);
			break;
		}
	}

}