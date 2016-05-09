package mas.com;

import java.io.Serializable;

import mas.agents.DummyExploAgent;
import mas.data.AgentInfo;

public class DataRequest implements Serializable {
	
	private Request requete;
	private AgentInfo info;
	private int leader_num;
	
	/*DataSend through network whenever i am asking for more*/
	public DataRequest(AgentInfo info, Request requete, int leader_num){
		this.info = info;
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
