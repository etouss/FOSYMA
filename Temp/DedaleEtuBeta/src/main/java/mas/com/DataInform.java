package mas.com;

import java.io.Serializable;

import mas.agents.AgentInfo;
import mas.agents.DummyExploAgent;

public class DataInform implements Serializable{
	private Inform inform;
	private AgentInfo info;
	private Object data;
	
	/*DataSend through network whenever i am asking for more*/
	public DataInform(DummyExploAgent agent, Inform inform, Object data){
		this.info = new AgentInfo(agent,0);
		this.inform = inform;
		this.data = data;
	}
	
	public Inform getInform(){
		return inform;
	}
	
	public AgentInfo getInfo(){
		return this.info;
	}
	
	public Object getData(){
		return data;
	}
}
