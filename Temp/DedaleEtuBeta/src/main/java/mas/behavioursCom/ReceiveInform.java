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
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.com.Confirm;
import mas.com.DataConfirm;
import mas.com.DataInform;
import mas.com.InfoPartage;
import mas.data.AgentInfo;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveInform extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private HashSet<AID> lack;
	private AgentLock un_move;
	private DummyExploAgent agent;

	private boolean finished=false;

	public ReceiveInform (final DummyExploAgent myagent,HashSet<AID> lack,AgentLock un_move) {
		super(myagent, 200);
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
				//agents_position_probability.remove(msg_rec.getSender());
				agent.getMeeting().update_meeting(data_info.getInfo(), when);
				switch(data_info.getInform()){
					case Graph:
						InfoPartage info_part  = (InfoPartage)data_info.getData();
						HashSet<Room> rooms = info_part.get_room_to_send();
						HashSet<AgentInfo> infos = info_part.get_agent_to_send();
						if(rooms != null){
							agent.getCastle().update_castle(rooms,((DummyExploAgent)this.myAgent).getWhen());
						}
						if(infos != null){
							agent.getMeeting().update_meeting(infos,((DummyExploAgent)this.myAgent).getWhen());
						}
						
						Statistique.graph_recu += 1;
						lack.add(msg.getSender());
					break;
					case Sending:
						//agents_position_probability.put(msg_rec.getSender(),when);
						un_move.set_lock_move(msg.getSender());
						//String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
						ACLMessage msg_s=new ACLMessage(ACLMessage.CONFIRM);
						msg_s.setSender(this.myAgent.getAID());
						msg_s.setContentObject(new DataConfirm(agent.getMeeting().get_me(),Confirm.Send,0));
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

