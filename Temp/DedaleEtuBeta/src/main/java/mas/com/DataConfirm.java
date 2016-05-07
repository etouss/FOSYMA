package mas.com;

import java.io.Serializable;

import mas.agents.AgentInfo;
import mas.agents.DummyExploAgent;

public class DataConfirm implements Serializable{
	private Confirm confirm;
	private AgentInfo info;
	private long hcode;
	
	/*DataSend through network whenever i am asking for more*/
	public DataConfirm(DummyExploAgent agent, Confirm confirm, long hcode){
		this.info = new AgentInfo(agent,0);
		this.confirm = confirm;
		this.hcode = hcode;
	}
	
	public Confirm getConfirm(){
		return confirm;
	}
	
	public AgentInfo getInfo(){
		return this.info;
	}
	
	public long getHcode(){
		return hcode;
	}
}
