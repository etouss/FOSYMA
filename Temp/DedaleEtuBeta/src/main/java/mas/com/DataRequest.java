package mas.com;

import java.io.Serializable;

import mas.agents.AgentInfo;
import mas.agents.DummyExploAgent;

public class DataRequest implements Serializable {
	
	private Request requete;
	private AgentInfo info;
	private int leader_num;
	
	/*DataSend through network whenever i am asking for more*/
	public DataRequest(DummyExploAgent agent, Request requete, int leader_num){
		this.info = new AgentInfo(agent,0);
		this.requete = requete;
		this.leader_num = leader_num;
	}
	
	public Request getRequete(){
		return requete;
	}
	
	public AgentInfo getInfo(){
		return this.info;
	}
	
	public int get_leader_num(){
		return leader_num;
	}
}
