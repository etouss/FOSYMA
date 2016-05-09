package mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import jade.core.AID;
import mas.data.AgentInfo;
import mas.data.AgentMode;
import mas.data.Meeting;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;

public class AgentDescisionMaker {
	
	private static final int STOP_MAX = 10;
	private static final int DETOUR = 5;
	private Meeting meeting;
	private Castle castle;
	private String previous = "";
	private int stop = 0;
	private int last_stop = 0;
	private boolean consensus = false;
	private String wumpus = "";
	
	public AgentDescisionMaker(Meeting meeting, Castle castle){
		this.castle = castle;
		this.meeting = meeting;
	}
	
	
	
	public String where_to_go(boolean trace){
			if(last_stop == 0 || last_stop > DETOUR || !trace){
				last_stop = 0;
				wumpus = "";
			}
			else{
				last_stop ++;
			}
		
			if(stop > STOP_MAX && trace){
				wumpus = previous;
				last_stop = 1;
			}	
			else if(stop > STOP_MAX && meeting.get_me().getAgentMode() != AgentMode.CONSENSUS_ASK){
				meeting.set_me_ask_consensus();
				return "";
			}
		switch(meeting.get_me().getAgentMode()){
			case EXPLORER:
				if(!meeting.is_best_bag())
				{
					for(Room r: castle.getTreasures()){
						if(meeting.notBlock(r)){
							meeting.set_me_blocker(r.getId());
							return "";
						}
					}
				}
				int max_not_block = 0;
				//String max_not_block_name = "";
				int min_block = 2000;
				String min_block_name = "";
				for(Room r: castle.getTreasures()){
					if(meeting.notBlock(r)){
						if(r.get_treasure_value()>max_not_block){
							max_not_block = r.get_treasure_value();
							//max_not_block_name = r.getId();
						}
					}
					else{
						if(r.get_treasure_value()<min_block){
							min_block = r.get_treasure_value();
							min_block_name = r.getId();
						}
					}
				}
				//System.out.println("Min:"+min_block);
				//System.out.println("Max:"+max_not_block);
				if(min_block!= 2000 && min_block < max_not_block){
					meeting.set_me_poster(min_block_name);
					return "";
				}
				if(castle.is_done_visited()){
					meeting.set_me_explo_done(castle.bestTreasure());
					return "";
				}
				return where_to_go_explo(wumpus,trace);
			case POSTER:
				System.out.println(meeting.get_me().getId()+" :::: POSTTTERRRR TTTTTRRRRYY");
				if(meeting.notBlock(meeting.get_me().getTarget()))
					meeting.set_me_explo();
				return where_to_go_spec(wumpus,trace);
			case EXPLORER_DONE:
				System.out.println(meeting.get_me().getId()+" :::: EXPLORER DONE");
				if(meeting.all_known()){
					meeting.set_me_catcher(algo_catch());
				}
				return where_to_go_spec(wumpus,trace);
			case CATCHER:
				return where_to_go_spec(wumpus,trace);
			case BLOCKER:
				if(!meeting.not_poster(meeting.get_me().getTarget())){
					meeting.set_me_explo();
					return "";
				}
				else if(consensus || meeting.get_me().on_spot()){
					if("".equals(meeting.consensusAsk())){
						consensus = false;
						return "";
					}
					if(meeting.consensusAsk() != null){
						consensus = true;
						return where_to_go_spec(wumpus,meeting.consensusAsk(),trace);
					}
					else{
						consensus = true;
						return where_to_go_explo(wumpus,trace);
					}
				}
				else if(!meeting.notBlock(meeting.get_me().getTarget())){//Si plus trésor !!
					meeting.set_me_explo();
					return "";
				}
				return where_to_go_spec(wumpus,trace);
			case CONSENSUS_ASK:
				if(meeting.get_me().getTarget() != null){
					return where_to_go_spec(wumpus,trace);
				}
				return where_to_go_explo(wumpus,trace);
			default:
				return "";
		}
	}
	
	public String algo_catch(){
		ArrayList<Room> treasures = castle.getTreasures();
		ArrayList<AgentInfo> agent_infos = new ArrayList<AgentInfo>();
		for(AgentInfo ai : meeting.getAgents().values()){
			agent_infos.add(ai);
		}
		int i = 0;
		//String target = "";
		do{
			treasures.sort((Room r1,Room r2)-> r1.get_treasure_value() - r2.get_treasure_value());
			agent_infos.sort((AgentInfo a1, AgentInfo a2) -> a1.getFreeSpace()-a2.getFreeSpace());
			Room r = treasures.remove(0);
			AgentInfo ai = agent_infos.remove(0);
			if(ai.getId().equals(meeting.get_me().getId())){
				return r.getId();
			}
			System.out.println(i);
			i++;
			
		}
		while(!treasures.isEmpty());
		/*Poster*/;
		return "";
	}
	
	
	public String where_to_go_spec(String wumpus,boolean trace){
		int deviance = 10;
		HashMap<Room,Integer> occupied = get_occupied_room();
		double max_reward = 0;
		String result = "";
		Room there = castle.get_room(meeting.get_me().getPosition(), -1);
		for(Room r_linked : there.getLinkedRooms()){
			if(r_linked.getId().equals(wumpus))continue;
			double reward = -1;
			Integer prob = occupied.get(r_linked);
			if(prob == null || prob < meeting.get_me().getWhen() - deviance)
				reward = r_linked.reward_spec(occupied,meeting.get_me().getWhen(),castle.get_room(meeting.get_me().getTarget()),wumpus);
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
		}
		if(previous.equals(result)){
			stop ++;
		}
		else{
			if(stop > STOP_MAX && trace){
				meeting.stop_consensus();
			}
			stop = 0;
		}
		previous = result;
		return result;
	}
	
	public String where_to_go_spec(String wumpus,String target,boolean trace){
		int deviance = 10;
		HashMap<Room,Integer> occupied = get_occupied_room();
		double max_reward = 0;
		String result = "";
		Room there = castle.get_room(meeting.get_me().getPosition(), -1);
		for(Room r_linked : there.getLinkedRooms()){
			if(r_linked.getId().equals(wumpus))continue;
			double reward = -1;
			Integer prob = occupied.get(r_linked);
			if(prob == null || prob < meeting.get_me().getWhen() - deviance)
				reward = r_linked.reward_spec(occupied,meeting.get_me().getWhen(),castle.get_room(target),wumpus);
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
		}
		if(previous.equals(result)){
			stop ++;
		}
		else{
			if(stop > STOP_MAX && trace){
				meeting.stop_consensus();
			}
			stop = 0;
		}
		previous = result;
		return result;
	}
	
	public String where_to_go_explo(String wumpus,boolean trace){
		int deviance = 10;
		HashMap<Room,Integer> occupied = get_occupied_room();
		double max_reward = 0;
		String result = "";
		Room there = castle.get_room(meeting.get_me().getPosition(), -1);
		for(Room r_linked : there.getLinkedRooms()){
			if(r_linked.getId().equals(wumpus))continue;
			double reward = -1;
			Integer prob = occupied.get(r_linked);
			if(prob == null || prob < meeting.get_me().getWhen() - deviance)
				reward = r_linked.reward(occupied,meeting.get_me().getWhen(),wumpus);
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
		}
		if(previous.equals(result)){
			stop ++;
		}
		else{
			if(stop > STOP_MAX && trace){
				meeting.stop_consensus();
			}
			stop = 0;
		}
		previous = result;
		return result;
	}
	
public HashMap<Room,Integer> get_occupied_room(){
		
		HashMap<Room,Integer> result = new HashMap<Room,Integer>();
		for(AgentInfo agent_info : meeting.getAgents().values()){
			Room r = castle.get_room(agent_info.getPosition(),-1);
			int prob = agent_info.getWhen();
			if(result.get(r) == null || result.get(r) < prob){
				result.remove(r);
				result.put(r,prob);
			}
		}
		return result;
	}
	
}
