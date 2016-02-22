package mas.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import mas.structuregraph.MyGraph;
import mas.agents.DummyExploAgent;



public class GiveGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private MyGraph graph;
	private DFAgentDescription[] listeAgents;
	
	public GiveGraphBehaviour (final Agent myagent,MyGraph graph, DFAgentDescription[] listeAgents) {
		super(myagent, 500);
		this.graph = graph;
		this.listeAgents = listeAgents;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(7);
		msg.setSender(this.myAgent.getAID());

		
		//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to send graph to its friends: "+graph.toString());
		try {
			msg.setContentObject(graph.ticked_send(((DummyExploAgent)this.myAgent).ticked()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for ( DFAgentDescription agent : listeAgents ){
			if (!agent.equals(this.myAgent.getAID()))
				msg.addReceiver(new AID(agent.toString(),AID.ISLOCALNAME));
		}

		((mas.abstractAgent)this.myAgent).sendMessage(msg);

		

	}

}