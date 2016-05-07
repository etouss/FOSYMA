package mas.com;

import java.io.Serializable;

import mas.agents.AgentInfo;
import mas.agents.DummyExploAgent;

public class DataRequest implements Serializable {
	
	private Request requete;
	private AgentInfo info;
	
	/*DataSend through network whenever i am asking for more*/
	public DataRequest(DummyExploAgent agent, Request requete){
		this.info = new AgentInfo(agent,0);
		this.requete = requete;
	}
	
	public Request getRequete(){
		return requete;
	}
	
	public AgentInfo getInfo(){
		return this.info;
	}
}
