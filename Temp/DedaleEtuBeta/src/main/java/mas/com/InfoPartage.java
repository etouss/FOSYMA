package mas.com;

import java.io.Serializable;
import java.util.HashSet;

import mas.agents.AgentInfo;
import mas.structuregraph.Room;

public class InfoPartage implements Serializable{
	
	private HashSet<Room> room_to_send;
	private HashSet<AgentInfo> agent_to_send;
	
	public InfoPartage(HashSet<Room> room_to_send,HashSet<AgentInfo> agent_to_send){
		this.room_to_send = room_to_send;
		this.agent_to_send = agent_to_send;
	}
	
	public HashSet<Room> get_room_to_send(){
		return room_to_send;
	}
	
	public HashSet<AgentInfo> get_agent_to_send(){
		return agent_to_send;
	}

}
