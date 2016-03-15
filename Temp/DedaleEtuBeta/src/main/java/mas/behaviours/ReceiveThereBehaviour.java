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
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveThereBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	//private Castle castle;
	private HashMap<AID,String> agents_position;
	private HashMap<AID,Integer> agents_position_probability;
	private AgentLock un_move;
	private HashMap<AID,Integer> hmack;
	private Castle castle;

	private boolean finished=false;

	public ReceiveThereBehaviour (final Agent myagent,Castle castle,HashMap<AID,Integer> hmack,HashMap<AID,String> agents_position,HashMap<AID,Integer> agents_position_probability,AgentLock un_move) {
		super(myagent, 200);
		this.agents_position = agents_position;
		this.agents_position_probability = agents_position_probability;
		this.un_move = un_move;
		this.hmack = hmack;
		this.castle = castle;
		//super(myagent);
	}


	public void onTick() {
		//((DummyExploAgent)this.myAgent).incWhen();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);			

		final ACLMessage msg_rec = this.myAgent.receive(msgTemplate);
		if (msg_rec != null) {		
			un_move.set_mail_check();
			if(msg_rec.getContent().contains("GiveGraph!")){
				//System.out.println(myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
				//int count = Integer.parseInt(msg.getContent());
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents_position.remove(msg_rec.getSender());
				agents_position_probability.remove(msg_rec.getSender());
				agents_position.put(msg_rec.getSender(),msg_rec.getContent().split("::")[1]);
				agents_position_probability.put(msg_rec.getSender(),when);
				//System.out.println(this.myAgent.getLocalName()+" ToSend :: "+msg.getSender());
				
				String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
				ACLMessage msg_sent=new ACLMessage(ACLMessage.REQUEST);
				msg_sent.setSender(this.myAgent.getAID());
				
				if(castle.have_to_send(hmack.get(msg_rec.getSender()))){
					System.out.println("Send"+hmack.size());
					msg_sent.setContent("Sending! ::"+myPosition);
					un_move.set_lock_move(msg_rec.getSender());
					Statistique.graph_propose ++;
				}
				else{
					//System.out.println("Synch");
					msg_sent.setContent("Synchro! ::"+myPosition);
				}
				
				msg_sent.addReceiver(msg_rec.getSender());
				((mas.abstractAgent)this.myAgent).sendMessage(msg_sent);
				
			}
			if(msg_rec.getContent().contains("There!")){
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents_position.remove(msg_rec.getSender());
				agents_position_probability.remove(msg_rec.getSender());
				agents_position.put(msg_rec.getSender(),msg_rec.getContent().split("::")[1]);
				agents_position_probability.put(msg_rec.getSender(),when);
			}
			if(msg_rec.getContent().contains("Sending!")){
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents_position.remove(msg_rec.getSender());
				agents_position_probability.remove(msg_rec.getSender());
				agents_position.put(msg_rec.getSender(),msg_rec.getContent().split("::")[1]);
				agents_position_probability.put(msg_rec.getSender(),when);
				un_move.set_lock_move(msg_rec.getSender());
				
				String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
				ACLMessage msg=new ACLMessage(ACLMessage.CONFIRM);
				msg.setSender(this.myAgent.getAID());
				msg.setContent("AckSending! ::"+myPosition);
				msg.addReceiver(msg_rec.getSender());
				((mas.abstractAgent)this.myAgent).sendMessage(msg);
				
			}
			if(msg_rec.getContent().contains("Synchro!")){
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents_position.remove(msg_rec.getSender());
				agents_position_probability.remove(msg_rec.getSender());
				agents_position.put(msg_rec.getSender(),msg_rec.getContent().split("::")[1]);
				agents_position_probability.put(msg_rec.getSender(),when);
			}
			
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

