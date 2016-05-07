package mas.behavioursCom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.AgentInfo;
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.com.Confirm;
import mas.com.DataConfirm;
import mas.com.DataInform;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveInform extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private Castle castle;
	private HashSet<AID> lack;
	private HashMap<AID,AgentInfo> agents;
	private AgentLock un_move;
	private DummyExploAgent agent;

	private boolean finished=false;

	public ReceiveInform (final DummyExploAgent myagent,Castle castle,HashSet<AID> lack,HashMap<AID,AgentInfo> agents,AgentLock un_move) {
		super(myagent, 200);
		this.castle = castle;
		this.agents = agents;
		this.un_move= un_move;
		this.lack = lack;
		this.agent = myagent;
	}


	public void onTick() {
		((DummyExploAgent)this.myAgent).incWhen();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			
			try {
				DataInform data_info = (DataInform) msg.getContentObject();
				int when = ((DummyExploAgent)this.myAgent).getWhen();
				agents.remove(msg.getSender());
				//agents_position_probability.remove(msg_rec.getSender());
				AgentInfo info_1 = data_info.getInfo();
				info_1.setWhen(when);
				agents.put(msg.getSender(),info_1);
				switch(data_info.getInform()){
					case Graph:
						HashSet<Room> rooms = (HashSet<Room>)data_info.getData();
						castle.update_castle(rooms,((DummyExploAgent)this.myAgent).getWhen());
						Statistique.graph_recu += 1;
						lack.add(msg.getSender());
					break;
					case Sending:
						//agents_position_probability.put(msg_rec.getSender(),when);
						un_move.set_lock_move(msg.getSender());
						//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
						ACLMessage msg_s=new ACLMessage(ACLMessage.CONFIRM);
						msg_s.setSender(this.myAgent.getAID());
						msg_s.setContentObject(new DataConfirm(this.agent,Confirm.Send,0));
						//msg.setContent("AckSending! ::"+myPosition);
						msg_s.addReceiver(msg.getSender());
						((mas.abstractAgent)this.myAgent).sendMessage(msg_s);
					break;
					case Synch:
					break;
				}	
			} catch (UnreadableException e) {
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

