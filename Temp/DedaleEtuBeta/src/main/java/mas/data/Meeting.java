package mas.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Room;

public class Meeting {
	
	/*Donné de réglagle*/
	//private static final int nb_tick_maximal = 0;
	
	/*Donnée fixé*/
	private AgentInfo me;
	private HashMap<AID,AgentInfo> agents;
	private int nb_agent;
	private int last_modif_data;
	private boolean best_bag = true;

	
	/*Donné potentiellement calculé et probabilisé*/
	
	public boolean not_poster(String target){
		for(AgentInfo ai :agents.values()){
			 if(ai.getAgentMode() == AgentMode.POSTER && ai.getTarget().equals(target)){
				 System.out.println("POSTER");
				 return false;
			 }
		 }
		 return true;
	}
	
	public boolean all_known(){
		return nb_agent == agents.size()+1;
	}
	
	public String consensusAsk(){
		for(AgentInfo ai :agents.values()){
			 if(ai.getAgentMode() == AgentMode.CONSENSUS_ASK && ai.getWhen() + 10 > me.getWhen()){
				 System.out.println(me.getId()+"BUG!!!");
				 return ai.getTarget();
			 }
		 }
		 return "";
	}
	
	public void set_me_poster(String target){
		me.set_me_poster(target);
	}
	
	public void stop_consensus(){
		me.stop_consensus();
	}
	
	public void set_me_ask_consensus(){
		me.set_me_consensus_ask();
	}
	
	public void set_me_blocker(String target){
		me.set_me_blocker(target);
	}
	
	public void set_me_explo_done(String target){
		me.set_me_explo_done(target);
	}
	
	public void set_me_catcher(String target){
		me.set_me_catcher(target);
	}
	
	public void set_me_explo(){
		me.set_me_explo();
	}
	
	public boolean is_best_bag(){
		return best_bag;
	}
	
	public boolean notBlock(Room r){
		 for(AgentInfo ai :agents.values()){
			 if(ai.getAgentMode() == AgentMode.BLOCKER && ai.getTarget().equals(r.getId()))
				 return false;
		 }
		 return true;
	}
	
	public boolean notBlock(String r){
		 for(AgentInfo ai :agents.values()){
			 if(ai.getAgentMode() == AgentMode.BLOCKER && ai.getTarget().equals(r))
				 return false;
		 }
		 return true;
	}
	
	public Meeting(DummyExploAgent me,int nb_agent){
		this.nb_agent = nb_agent;
		this.me = new AgentInfo(me);
		this.agents = new HashMap<AID,AgentInfo>();
	}
	
	public void update_me(DummyExploAgent me_agent){
		me.update(me_agent);
	}
	public AgentInfo get_me(){
		return me;
	}
	
	public HashMap<AID,AgentInfo> getAgents(){
		return agents;
	}
	
	public void update_meeting(HashSet<AgentInfo> networks,int when){
		for(AgentInfo net: networks){
			if(agents.containsKey(net.getId())){
				if(agents.get(net.getId()).updateAgentInfo(net, when)){
					last_modif_data = when;
					if(net.getFreeSpace()>me.getFreeSpace()){
						best_bag = false;
					}
				}
			}
			else{
				agents.put(net.getId(),net);
				last_modif_data = when;
				if(net.getFreeSpace()>me.getFreeSpace()){
					best_bag = false;
				}
			}
		}
	}
	
	public void update_meeting(AgentInfo net,int when){
		if(agents.containsKey(net.getId())){
			if(agents.get(net.getId()).updateAgentInfo(net, when)){
				last_modif_data = when;
				if(net.getFreeSpace()>me.getFreeSpace()){
					best_bag = false;
				}
			}
		}
		else{
			agents.put(net.getId(),net);
			last_modif_data = when;
			if(net.getFreeSpace()>me.getFreeSpace()){
				best_bag = false;
			}
		}
		
	}
	
	public HashSet<AgentInfo> info_to_send(int when, AID to_who){
		HashSet<AgentInfo> result = new HashSet<AgentInfo>();
		for(AgentInfo info : agents.values()){
			if(!to_who.equals(info.getId()) && info.getWhen()>when){
				result.add(info);
			}
		}
		return result;
	}
	
	public boolean have_to_send(int when){
		return when < last_modif_data;
	}
}
