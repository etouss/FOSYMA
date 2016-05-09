package mas.behaviour;

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
import mas.data.AgentInfo;
import mas.data.AgentMode;
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
	
	private HashSet<AID> lsend;
	private HashSet<AID> lack;
	private AgentLock un_move;
	private DummyExploAgent agent;

	public Walk (final DummyExploAgent myagent,HashSet<AID> lsend,HashSet<AID> lack,AgentLock un_move) {
		super(myagent, 500);
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
			Room self = agent.getCastle().get_room(myPosition,when);
			
			for(Couple<String,List<Attribute>> couple : lobs ){
				if(couple.getLeft().equals(myPosition))continue;
				Room linked_room = agent.getCastle().get_room(couple.getLeft(),when);
				agent.getCastle().add_link(self,linked_room,when);
				linked_room.hash_room();
			}
			self.setVisited(when);
			self.hash_room();
			agent.getCastle().setThere(self);
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
				case STENCH:
					b = true;
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
			
			if(((mas.abstractAgent)this.myAgent).moveTo(agent.getDM().where_to_go(b))){
				return;
			}
			
			
			/*Check Dead pour savoir si je me met a bouger !*/
			
			
			/*
			 * 
			 * Pas le droit de bloquer si je suis le plus grand pack que je connais.
			 * Sauf si wumpus pas loin.
			 * 
			 * 
			 * Je connais au moins 1 agents je peux bloquer et je le fait des que je rencontre/connais un trésor (le plus grand de ce que je connais).
			 * Ce qui sont les agents qui sont les plus grand continue l'explo et informe les plus petit ----> allez retour.
			 * Quand exploration fini.
			 * 
			 * 
			
			*/
	
	
		}
	}
}