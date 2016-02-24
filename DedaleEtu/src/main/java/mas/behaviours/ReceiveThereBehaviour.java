package mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.DummyExploAgent;
import mas.structuregraph.MyGraph;
import mas.structuregraph.Node_not_known_Exeption;
import mas.structuregraph.Noeud;
import mas.utility.TupleAidTick;



public class ReceiveThereBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private MyGraph graph;
	private HashSet<TupleAidTick> ll;

	private boolean finished=false;

	public ReceiveThereBehaviour (final Agent myagent,HashSet<TupleAidTick> ll) {
		super(myagent, 200);
		this.ll = ll;
		//super(myagent);
	}


	public void onTick() {
		((DummyExploAgent)this.myAgent).ticking();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			System.out.println(myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
			int count = Integer.parseInt(msg.getContent());
			ll.add(new TupleAidTick(msg.getSender(),count));
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

