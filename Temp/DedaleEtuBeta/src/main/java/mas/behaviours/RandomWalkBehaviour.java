package mas.behaviours;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Couple;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.AgentLock;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import statistique.Statistique;

/**************************************
 * 
 * 
 * 				BEHAVIOUR
 * 
 * 
 **************************************/


public class RandomWalkBehaviour extends TickerBehaviour{
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private Castle castle;
	private Random r= new Random();
	private int count = 0;
	private HashSet<AID> lsend;
	private HashSet<AID> lack;
	private AgentLock un_move;

	public RandomWalkBehaviour (final mas.abstractAgent myagent,Castle castle,HashSet<AID> lsend,HashSet<AID> lack,AgentLock un_move) {
		super(myagent, 500);
		this.castle = castle;
		this.lsend = lsend;
		this.lack = lack;
		this.un_move = un_move;
		
		//super(myagent);
	}

	@Override
	public void onTick() {
		
		((DummyExploAgent)this.myAgent).incWhen();
		int when = ((DummyExploAgent)this.myAgent).getWhen();
		
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
			}
			*/


			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();

			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Attribute a:lattribute){
				switch (a) {
				case TREASURE:
					System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("Value of the treasure on the current position: "+a.getValue());
					
					(((DummyExploAgent)this.myAgent).getCastle().get_room(myPosition)).set_treasure_value(Integer.parseInt(a.getValue().toString()));
					
					System.out.println("The agent grabbed :"+((mas.abstractAgent)this.myAgent).pick());
					System.out.println("the remaining backpack capacity is: "+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("The value of treasure on the current position: (unchanged before a new call to observe()): "+a.getValue());
					b=true;
					break;

				default:
					break;
				}
			}

			//If the agent picked (part of) the treasure
			if (b){
				List<Couple<String,List<Attribute>>> lobs2=((mas.abstractAgent)this.myAgent).observe();//myPosition
				System.out.println("lobs after picking "+lobs2);
			}
			
			
			/*
			boolean ok = false;
			for(i = 0;i<lobs.size();i++){
				if(!castle.is_visited(lobs.get(i).getLeft())){
					ok = true;
					break;
				}
			}
			//Random move from the current position
			if(!ok){
				i=r.nextInt(lobs.size());
			}
			*/
			//The move action (if any) should be the last action of your behaviour
			
			if(!lsend.isEmpty() && !lack.isEmpty())return;
			
			//String where = castle.where_to_to_page(myPosition,when);
			
			if(!un_move.is_ready_move()){
				System.out.println("Waiting ");
				return;
			}
			
			if(castle.is_done_visited()){
				System.out.println("Propose :"+Statistique.graph_propose+" Send :"+Statistique.graph_envoye+" Recu : "+Statistique.graph_recu+" Ack :"+Statistique.graph_ack);
				System.out.println(myAgent.getLocalName()+" is done !!" + Statistique.count);
				/*int i = r.nextInt(lobs.size());
				while(!((mas.abstractAgent)this.myAgent).moveTo(lobs.get(i).getLeft())){
					i=r.nextInt(lobs.size());
				}
				*/
				return;
			}
			count ++;
			Statistique.count ++;
			
			if(((mas.abstractAgent)this.myAgent).moveTo(castle.where_to_to_heavy(myPosition,when))){
				return;
			}
			//System.out.println("Deg");
			
			/*
			}*/
		}

	}

}