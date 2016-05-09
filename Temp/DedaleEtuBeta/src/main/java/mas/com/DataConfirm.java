package mas.com;

import java.io.Serializable;

import mas.agents.DummyExploAgent;
import mas.data.AgentInfo;

public class DataConfirm implements Serializable{
	private Confirm confirm;
	private AgentInfo info;
	private long hcode;
	
	/*DataSend through network whenever i am asking for more*/
	public DataConfirm(AgentInfo info, Confirm confirm, long hcode){
		this.info = info;
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
