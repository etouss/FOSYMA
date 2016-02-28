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
	private HashMap<AID,String> agents_position;
	private HashMap<AID,Integer> agents_position_probability;

	private boolean finished=false;

	public ReceiveThereBehaviour (final Agent myagent,HashSet<AID> lsend,HashMap<AID,String> agents_position,HashMap<AID,Integer> agents_position_probability) {
		super(myagent, 200);
		this.lsend = lsend;
		this.agents_position = agents_position;
		this.agents_position_probability = agents_position_probability;
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
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				lsend.add(msg.getSender());
				agents_position.remove(msg.getSender());
				agents_position_probability.remove(msg.getSender());
				agents_position.put(msg.getSender(),msg.getContent().split("::")[1]);
				agents_position_probability.put(msg.getSender(),when);
			}
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

