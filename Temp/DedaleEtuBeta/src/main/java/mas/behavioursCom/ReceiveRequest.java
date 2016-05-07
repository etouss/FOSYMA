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
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.AgentInfo;
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.com.Confirm;
import mas.com.DataConfirm;
import mas.com.DataInform;
import mas.com.DataRequest;
import mas.com.Inform;
import mas.com.Request;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveRequest extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	//private Castle castle;
	private HashMap<AID,AgentInfo> agents;
	//private HashMap<AID,Integer> agents_position_probability;
	private AgentLock un_move;
	private HashMap<AID,Integer> hmack;
	private Castle castle;
	private DummyExploAgent agent;

	private boolean finished=false;

	public ReceiveRequest (final DummyExploAgent myagent,Castle castle,HashMap<AID,Integer> hmack,HashMap<AID,AgentInfo> agents,AgentLock un_move) {
		super(myagent, 200);
		this.agents = agents;
		this.agent = myagent;
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
			try {
				DataRequest data = (DataRequest) msg_rec.getContentObject();
			switch(data.getRequete()){
			case Graph:
				//System.out.println(myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
				//int count = Integer.parseInt(msg.getContent());
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents.remove(msg_rec.getSender());
				//agents_position_probability.remove(msg_rec.getSender());
				AgentInfo info = data.getInfo();
				info.setWhen(when);
				agents.put(msg_rec.getSender(),info);
				//agents_position_probability.put(msg_rec.getSender(),when);
				//System.out.println(this.myAgent.getLocalName()+" ToSend :: "+msg.getSender());
				
				String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
				ACLMessage msg_sent=new ACLMessage(ACLMessage.INFORM);
				msg_sent.setSender(this.myAgent.getAID());
				
				if(castle.have_to_send(hmack.get(msg_rec.getSender()))){
					//System.out.println("Send"+hmack.size());
					msg_sent.setContentObject(new DataInform(this.agent,Inform.Sending,null));
					un_move.set_lock_move(msg_rec.getSender());
					Statistique.graph_propose ++;
				}
				else{
					//System.out.println("Synch");
					msg_sent.setContentObject(new DataInform(this.agent,Inform.Synch,null));
				}
				
				msg_sent.addReceiver(msg_rec.getSender());
				((mas.abstractAgent)this.myAgent).sendMessage(msg_sent);
				break;
				
			}
			/*
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
			*/
			}
			catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}
