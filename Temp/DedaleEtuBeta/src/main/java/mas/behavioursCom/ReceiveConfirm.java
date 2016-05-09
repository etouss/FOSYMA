package mas.behavioursCom;

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
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.com.DataConfirm;
import mas.com.DataRequest;
import mas.data.AgentInfo;
import mas.data.AgentMode;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveConfirm extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private HashSet<AID> lsend;
	private HashMap<AID,Integer> hmsend;
	private HashMap<AID,Integer> hmack;
	private HashMap<AID,Long> hmcode;
	private AgentLock un_move;
	private DummyExploAgent agent;

	private boolean finished=false;
	

	public ReceiveConfirm (final DummyExploAgent myagent,HashSet<AID> lsend,HashMap<AID,Integer> hmsend,HashMap<AID,Integer> hmack,HashMap<AID,Long> hmcode,AgentLock un_move) {
		super(myagent, 200);
		this.hmsend = hmsend;
		this.hmack = hmack;
		this.hmcode=hmcode;
		this.un_move = un_move;
		this.lsend = lsend;
		this.agent = myagent;
		
		//super(myagent);
	}


	public void onTick() {
		//((DummyExploAgent)this.myAgent).incWhen();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			un_move.set_mail_check();
			try {
				DataConfirm data = (DataConfirm) msg.getContentObject();
			
			switch(data.getConfirm()){
				case Graph:
					deal_with_graph(msg,data);
					break;
				case Send:
					deal_with_send(msg,data);
					break;

			}
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}
	
	private void deal_with_graph(ACLMessage msg,DataConfirm data){
		//System.out.println(myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
		//int count = Integer.parseInt(msg.getContent());
		Statistique.graph_ack += 1;
		int when = ((DummyExploAgent)this.myAgent).getWhen();
		long hcode = data.getHcode();
		if(hmcode.get(msg.getSender()) > hcode){
			System.out.println("Echec");
			return;
		}
		//System.out.println(msg.getSender() + " a Reussie a partir de "+ myAgent.getLocalName());
		System.out.println("Propose :"+Statistique.graph_propose+" Send :"+Statistique.graph_envoye+" Recu : "+Statistique.graph_recu+" Ack :"+Statistique.graph_ack);
		hmack.remove(msg.getSender());
		hmack.put(msg.getSender(),hmsend.get(msg.getSender()));
		agent.getMeeting().update_meeting(data.getInfo(), when);
		un_move.unset_lock_move(msg.getSender());
	}
	
	private void deal_with_send(ACLMessage msg,DataConfirm data){
		int when = ((DummyExploAgent)this.myAgent).getWhen();
		agent.getMeeting().update_meeting(data.getInfo(), when);
		lsend.add(msg.getSender());
		un_move.set_lock_move(msg.getSender());
	}
	
	/*
	private void deal_with_safe(ACLMessage msg,DataConfirm data){
		if(this.agent.getMode() != AgentMode.SAFE){
			System.out.println("WEIRD");
			return;
		}
		this.agent.add_contrainte(msg.getSender(),null);
		
	}
	*/

}

