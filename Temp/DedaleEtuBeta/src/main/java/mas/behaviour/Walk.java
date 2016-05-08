package mas.behaviour;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Couple;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.AgentInfo;
import mas.agents.AgentLock;
import mas.agents.AgentMode;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;

/******************************************
 * 
 * 
 * 				BEHAVIOUR
 * 
 * 
 ******************************************/


public class Walk extends TickerBehaviour{
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private static final long BLOQUAGE_MAX = 10;
	
	private Castle castle;
	private Random r= new Random();
	//private int count = 0;
	private HashSet<AID> lsend;
	private HashSet<AID> lack;
	private AgentLock un_move;
	private DummyExploAgent agent;
	private int bloquage = 0;

	public Walk (final DummyExploAgent myagent,Castle castle,HashSet<AID> lsend,HashSet<AID> lack,AgentLock un_move) {
		super(myagent, 500);
		this.castle = castle;
		this.lsend = lsend;
		this.lack = lack;
		this.un_move = un_move;
		this.agent = myagent;
		
		//super(myagent);
	}

	@Override
	public void onTick() {
		agent.incWhen();
		//((DummyExploAgent)this.myAgent).updateValue();
		int when = agent.getWhen();
		
		//Example to retrieve the current position
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		//System.out.println(myAgent.getLocalName()+" moved !!" + myPosition);
		if (myPosition!=""){
			//List of observable from the agent's current position
			List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();//myPosition
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			/*Add to graph*/
			Room self = castle.get_room(myPosition,when);
			
			for(Couple<String,List<Attribute>> couple : lobs ){
				if(couple.getLeft().equals(myPosition))continue;
				Room linked_room = castle.get_room(couple.getLeft(),when);
				castle.add_link(self,linked_room,when);
				linked_room.hash_room();
			}
			self.setVisited(when);
			self.hash_room();
			castle.setThere(self);
			//if(self.setVisited(when))
			//	castle.page_ranking_reset();

			//Little pause to allow you to follow what is going on
			/*
			try {
				System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}*/



			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();

			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Attribute a:lattribute){
				switch (a) {
				case TREASURE:
					//System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					//System.out.println("Value of the treasure on the current position: "+a.getValue());
					
					self.set_treasure_value((Integer) a.getValue(),when);
					/*
					try {
						System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
						System.in.read();
					} catch (IOException e) {
						e.printStackTrace();
					}
					*/
					break;
				default:
					break;
				}
			}
			
			
			/*J'ai le droit de bouger / changer ssi mailbox vide*/
			if(!lsend.isEmpty() && !lack.isEmpty())return;
			
			
			if(!un_move.is_ready_move()){
				System.out.println("Waiting ");
				return;
			}
			
			//System.out.println(agent.getAID()+ ":::: "+agent.getAgents().size());
			
			
			/*On détermine le mode dans lequelle on doit se placer*/
			
			/*On choisit alors le déplacement adapter*/
			
			//agent.dm(self);
			/*
			if(castle.is_done_visited()){
				System.out.println("Propose :"+Statistique.graph_propose+" Send :"+Statistique.graph_envoye+" Recu : "+Statistique.graph_recu+" Ack :"+Statistique.graph_ack);
				System.out.println(myAgent.getLocalName()+" is done !!" + Statistique.count);
				return;
			}
			count ++;
			Statistique.count ++;
			*/
			
			switch(agent.getMode()){
			case EXPLORER:
				
				/*
				if(((mas.abstractAgent)this.myAgent).moveTo(castle.where_to_go_explo(myPosition,when))){
					bloquage = 0;
					return;
				}
				bloquage ++;
				if(bloquage > BLOQUAGE_MAX){
					agent.setSafe(0,null);
					bloquage = 0;
				}*/
				break;
			case BLOCKER:
				break;
			case CATCHER:
				break;
			case COMMUNICATOR:
				break;
			case SAFE:
				break;
			case SAFE_READY:
				if(((mas.abstractAgent)this.myAgent).moveTo(agent.getTarget())){
					bloquage = 0;
					this.agent.setExplo();
					return;
				}
				break;
			case UNDEFINED:
				break;
			default:
				break;
			}
			
			
			
			/*
			}*/
		}

	}
	
	public void to_do_when_explo(){
		castle.get_treasures().sort((Room r1, Room r2) -> r1.get_treasure_value() - r2.get_treasure_value());
		for(Room r : castle.get_treasures()){
			/*Je deviens bloqeur*/
			if(r.getId().equals(agent.getCurrentPosition()) && r.get_treasure_value() > agent.getBackPackFreeSpace()){
				agent.become_blocker();
				castle.get_treasures().remove(r);
				break;
			}
			else if(r.get_treasure_value() > agent.getBackPackFreeSpace()){
				castle.get_treasures().remove(r);
				continue;
			}
			else if(r.get_treasure_value() <= agent.getBackPackFreeSpace()){
				boolean mine = true;
				for(AgentInfo ai : agent.getAgents().values()){
					if(ai.getFreeSac() >= r.get_treasure_value() && r.get_treasure_value() < agent.getBackPackFreeSpace()){
						mine = false;
						break;
					}
				}
				if(mine){
					agent.become_catcher(r);
					break;
				}
			}
		}
		/*Stay explo*/
	}

}