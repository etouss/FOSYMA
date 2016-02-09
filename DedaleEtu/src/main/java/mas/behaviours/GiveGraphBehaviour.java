package mas.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import mas.structuregraph.MyGraph;
import mas.agents.DummyExploAgent;

public class GiveGraphBehaviour extends TickerBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private MyGraph graph;
	
	public GiveGraphBehaviour (final Agent myagent,MyGraph graph) {
		super(myagent, 500);
		this.graph = graph;
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
		/*Autrement ca va pas du tout, Les annuaire ??!!*/
		if (!myAgent.getLocalName().equals("Explo1")){
			msg.addReceiver(new AID("Explo1",AID.ISLOCALNAME));
		}else{
			msg.addReceiver(new AID("Explo2",AID.ISLOCALNAME));
		}

		((mas.abstractAgent)this.myAgent).sendMessage(msg);

		

	}

}