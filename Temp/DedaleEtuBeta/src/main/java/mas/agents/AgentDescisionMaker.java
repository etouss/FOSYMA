package mas.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import jade.core.AID;
import mas.data.Data;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;

public class AgentDescisionMaker {
	
	private static final double Target_already_selected = 1;
	private static final double Treasure = 1;
	private static final double Better_Back_Pack_Agent = 1;
	private static final double Blocker_Agent = 1;
	private static final double Full_Back_Pack = 1;
	private static final double Full_Explo = 1;
	private static final double Knownledge = 1; /*Valeur purement empirique qui dépend de la carte*/
	private static final double Loose_Treasure = 1;
	private static final double Block_Treasure_Sup = 1;
	private static final double Block_Treasure_Inf = 1;
	
	private DummyExploAgent me;
	private Data data;
	
	public AgentDescisionMaker(DummyExploAgent agent){
		this.me = agent;
		this.data = agent.getData();
	}
	
	public void on_treasure(Room r){
		if(r.get_treasure_value() <= 0) return;
		double explorer_reward = 0; /*Calcul*/
		double blocker_reward = 0; /*Calcul*/
		double catcher_reward = 0; /*Calcul*/
		
		//explorer_reward = 1-(me.getWhen()/(Tick_explo_moyen + me.getWhen()));
		explorer_reward = 1.0 - Knownledge*data.knowledge_validity;
		double act_reward = 1.0 - Treasure*data.total_back_pack_free/(data.total_back_pack_free+r.get_treasure_value());
		
		if(act_reward > explorer_reward){
			catcher_reward = 1.0 - Loose_Treasure*Math.abs(me.getBackPackFreeSpace()-r.get_treasure_value())/data.total_back_pack;
			if(me.getBackPackFreeSpace() < r.get_treasure_value()){
				blocker_reward = 1.0 - Block_Treasure_Sup*(data.best_pack_prob - me.getBackPackFreeSpace())/data.best_pack_prob;
			}
			else {
				blocker_reward = 1.0 - Block_Treasure_Inf*r.get_treasure_value()/(r.get_treasure_value()+data.mean_back_pack_free);
			}
		}
		
		System.out.println("Explo :"+explorer_reward);
		System.out.println("Act :"+act_reward);
		System.out.println("Catch :"+catcher_reward);
		System.out.println("Block :"+blocker_reward);
		
		
		
	}
	
	
}
