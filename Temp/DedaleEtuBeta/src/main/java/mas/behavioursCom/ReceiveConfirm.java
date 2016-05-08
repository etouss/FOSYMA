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
import mas.agents.AgentInfo;
import mas.agents.AgentLock;
import mas.agents.AgentMode;
import mas.agents.DummyExploAgent;
import mas.com.DataConfirm;
import mas.com.DataRequest;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveConfirm extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	//private Castle castle;
	private HashSet<AID> lsend;
	private HashMap<AID,Integer> hmsend;
	private HashMap<AID,Integer> hmack;
	//private HashMap<AID,String> agents_position;
	private HashMap<AID,AgentInfo> agents;
	private HashMap<AID,Long> hmcode;
	private AgentLock un_move;
	private DummyExploAgent agent;

	private boolean finished=false;
	

	public ReceiveConfirm (final DummyExploAgent myagent,HashSet<AID> lsend,HashMap<AID,Integer> hmsend,HashMap<AID,Integer> hmack,HashMap<AID,AgentInfo> agents,HashMap<AID,Long> hmcode,AgentLock un_move) {
		super(myagent, 200);
		this.hmsend = hmsend;
		this.hmack = hmack;
		this.agents = agents;
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
				case ConfirmSafe:
					deal_with_safe(msg,data);
					break;
				default:
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
		agents.remove(msg.getSender());
		//agents_position_probability.remove(msg.getSender());
		//System.out.println(msg.getContent().split("::")[1]);
		AgentInfo info = data.getInfo();
		info.setWhen(when);
		agents.put(msg.getSender(),info);
		//agents_position_probability.put(msg.getSender(),when);
		un_move.unset_lock_move(msg.getSender());
	}
	
	private void deal_with_send(ACLMessage msg,DataConfirm data){
		int when_2 = ((DummyExploAgent)this.myAgent).getWhen();
		agents.remove(msg.getSender());
		AgentInfo info_2 = data.getInfo();
		info_2.setWhen(when_2);
		agents.put(msg.getSender(),info_2);
		lsend.add(msg.getSender());
		un_move.set_lock_move(msg.getSender());
	}
	
	private void deal_with_safe(ACLMessage msg,DataConfirm data){
		if(this.agent.getMode() != AgentMode.SAFE){
			System.out.println("WEIRD");
			return;
		}
		this.agent.add_contrainte(msg.getSender(),null);
		/*Trés bien je creer ma liste d'agent / target*/
		
	}


}

