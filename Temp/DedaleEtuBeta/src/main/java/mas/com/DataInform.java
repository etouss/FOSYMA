package mas.com;

import java.io.Serializable;

import mas.agents.DummyExploAgent;
import mas.data.AgentInfo;

public class DataInform implements Serializable{
	private Inform inform;
	private AgentInfo info;
	private Object data;
	
	/*DataSend through network whenever i am asking for more*/
	public DataInform(AgentInfo info, Inform inform, Object data){
		this.info = info;
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
