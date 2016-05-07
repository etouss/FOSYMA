package mas.agents;

import java.io.Serializable;

import jade.core.AID;

public class AgentInfo implements Serializable{
	
	private transient int when_my_tick;
	
	private AID id;
	private String position;
	//private int  timestamp;
	private int sacados;
	private int when_his_tick;
	private AgentMode mode;
	private String target;
	private int delta;
	private int total_sac;
	
	public AgentInfo(DummyExploAgent agent,int delta){
		id = agent.getAID();
		position = agent.getCurrentPosition();
		sacados = agent.getBackPackFreeSpace();
		when_his_tick = agent.getWhen();
		mode = agent.getMode();
		target = agent.getTarget();
		this.total_sac = agent.totalbackpack;
		this.delta = delta;
	}
	
	public String getPosition(){
		return position;
	}
	
	public int getTotalSac(){
		return total_sac;
	}
	public int getFreeSac(){
		return sacados;
	}
	
	public void setWhen(int when){
		this.when_my_tick = when;
	}
	public AgentMode getAgentMode(){
		return mode;
	}
	
	public String getTarget(){
		return target;
	}
	
	public int getDelta(int when){
		return delta+(when - when_my_tick);
	}
	
	public int getWhen(){
		return when_his_tick;
	}

	public int getWhen_my_tick() {
		return when_my_tick - delta;
	}
	
}
