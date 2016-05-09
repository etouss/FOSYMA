package mas.com;

import java.io.Serializable;
import java.util.HashSet;

import jade.core.AID;
import mas.agents.DummyExploAgent;
import mas.data.AgentInfo;
import mas.structuregraph.Room;

public class InfoPartage implements Serializable{
	
	private HashSet<Room> room_to_send;
	private HashSet<AgentInfo> agent_to_send;
	//private AgentInfo order;
	
	public InfoPartage(DummyExploAgent me,int when,AID who){
		this.room_to_send = me.getCastle().room_to_send(when);
		this.agent_to_send = me.getMeeting().info_to_send(when,who);
		//this.order = null;
	}
	
	public InfoPartage(DummyExploAgent me,int when,AID who,AgentInfo order){
		this.room_to_send = me.getCastle().room_to_send(when);
		this.agent_to_send = me.getMeeting().info_to_send(when,who);
		//this.order = order;
	}
	
	public HashSet<Room> get_room_to_send(){
		return room_to_send;
	}
	
	public HashSet<AgentInfo> get_agent_to_send(){
		return agent_to_send;
	}

}
