package mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Environment.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.DummyExploAgent;
import mas.structuregraph.MyGraph;
import mas.structuregraph.Node_not_known_Exeption;
import mas.structuregraph.Noeud;



public class ReceiveGraphBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private MyGraph graph;

	private boolean finished=false;

	public ReceiveGraphBehaviour (final Agent myagent,MyGraph graph) {
		super(myagent, 200);
		this.graph = graph;
		//super(myagent);
	}


	public void onTick() {
		((DummyExploAgent)this.myAgent).ticking();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			//System.out.println("<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
			//this.finished=true;
			try {
				graph.synchro((ArrayList<Noeud>)msg.getContentObject(),((DummyExploAgent)this.myAgent).ticked());
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Agent "+this.myAgent.getLocalName()+ " updated his graph: "+graph.toString());
			
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

