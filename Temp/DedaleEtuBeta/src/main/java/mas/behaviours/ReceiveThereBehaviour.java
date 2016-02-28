package mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Couple;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Room;



public class ReceiveThereBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	//private Castle castle;
	private HashSet<AID> lsend;
	private HashMap<String,Room> agents_position;
	private HashMap<String,Integer> agents_position_probability;

	private boolean finished=false;

	public ReceiveThereBehaviour (final Agent myagent,HashSet<AID> lsend) {
		super(myagent, 200);
		this.lsend = lsend;
		//super(myagent);
	}


	public void onTick() {
		((DummyExploAgent)this.myAgent).incWhen();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			if(msg.getContent().contains("GiveGraph!")){
				//System.out.println(myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
				//int count = Integer.parseInt(msg.getContent());
				lsend.add(msg.getSender());
			}
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

