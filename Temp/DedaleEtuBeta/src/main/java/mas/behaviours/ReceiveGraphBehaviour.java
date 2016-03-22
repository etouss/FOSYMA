package mas.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;



public class ReceiveGraphBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;
	private Castle castle;
	private HashSet<AID> lack;

	private boolean finished=false;

	public ReceiveGraphBehaviour (final Agent myagent,Castle castle,HashSet<AID> lack) {
		super(myagent, 200);
		this.castle = castle;
		this.lack = lack;
		//super(myagent);
	}


	public void onTick() {
		((DummyExploAgent)this.myAgent).incWhen();
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			//System.out.println("\n" +myAgent.getLocalName()+"<----Graph received from "+msg.getSender().getLocalName());
			//System.out.println("\n Agent "+this.myAgent.getLocalName()+ " prior his graph: "+graph.toString());
			//this.finished=true;
			try {
				HashSet<Room> rooms = (HashSet<Room>)msg.getContentObject();
				castle.update_castle(rooms,((DummyExploAgent)this.myAgent).getWhen());
				Statistique.graph_recu += 1;
				/*for(Room r : rooms){
					System.out.print("["+r.getId()+"] ");
				}
				System.out.print("\n");*/
				lack.add(msg.getSender());
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("\n Agent "+this.myAgent.getLocalName()+ " updated his graph: "+graph.toString());
			
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}


}

