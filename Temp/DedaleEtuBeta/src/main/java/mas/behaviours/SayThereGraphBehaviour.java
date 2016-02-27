package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import mas.agents.*;


public class SayThereGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	//private MyGraph graph;
	private DFAgentDescription[] listeAgents;
	
	public SayThereGraphBehaviour (final Agent myagent, DFAgentDescription[] listeAgents) {
		super(myagent, 500);
		//this.graph = graph;
		this.listeAgents = listeAgents;
		//super(myagent);
	}

	@Override
	public void onTick() {
		//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.myAgent.getAID());
		msg.setContent("GiveGraph!");
		
		for ( DFAgentDescription agent : listeAgents ){
			if (!agent.getName().equals(this.myAgent.getAID()))
				msg.addReceiver(agent.getName());
		}
		((mas.abstractAgent)this.myAgent).sendMessage(msg);

		

	}

}