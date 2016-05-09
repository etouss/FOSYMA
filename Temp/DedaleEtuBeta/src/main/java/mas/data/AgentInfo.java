package mas.data;

import java.io.Serializable;

import jade.core.AID;
import mas.agents.DummyExploAgent;
import mas.structuregraph.Room;

public class AgentInfo implements Serializable{
	
	private AID id;
	private int when_his_tick;
	private transient int when_my_tick;
	private int free_space;
	private int total_space;
	private AgentMode mode = AgentMode.EXPLORER;
	private String target = null;
	private String position = null;
	private transient AgentMode last_mode = AgentMode.EXPLORER;
	//private int  timestamp;
	//private int sacados;
	//private int when_his_tick;
	//private Room target;
	//private int delta;
	
	
	public void set_me_poster(String target){
		System.out.println("I AM A POSTER" + target);
		this.mode = AgentMode.POSTER;
		this.target = target;
	}
	
	public void set_me_explo(){
		this.mode = AgentMode.EXPLORER;
		this.target = null;
	}
	
	public void stop_consensus(){
		this.mode = last_mode;
	}
	
	public void set_me_consensus_ask(){
		//System.out.println("I AM A CONSENSUS" + target);
		this.last_mode = mode;
		this.mode = AgentMode.CONSENSUS_ASK;
	}
	
	public void set_me_explo_done(String target){
		this.mode = AgentMode.EXPLORER_DONE;
		this.target = target;
	}
	
	public void set_me_catcher(String target){
		System.out.println("I AM A Catcher" + target);
		this.mode = AgentMode.CATCHER;
		this.target = target;
	}
	
	public void set_me_blocker(String target){
		System.out.println(this.getId()+" :::: Blocker");
		this.mode = AgentMode.BLOCKER;
		this.target = new String(target);
	}
	
	public boolean on_spot(){
		return target.equals(position);
	}
	
	public AgentInfo(DummyExploAgent agent){
		id = agent.getAID();
		free_space = agent.getBackPackFreeSpace();
		total_space = agent.getBackPackFreeSpace();
		position = agent.getCurrentPosition();
		when_his_tick = agent.getWhen();
	}
	
	/*Un choix pour eviter la redondance !!! (On renvoi pas si juste update qualité info)*/
	public boolean updateAgentInfo(AgentInfo network,int when){
		if(network.getHisWhen() <= when_his_tick){
			return false;
		}
		//if(network.getAgentMode() == AgentMode.BLOCKER)System.out.println("JE COMPREND PAS TOUT");
		boolean retour = false;
		when_his_tick = network.getHisWhen();
		if(mode != network.getAgentMode()){
			mode = network.getAgentMode();
			when_my_tick = when;
			retour =  true;
		}
		if(target != null && !target.equals(network.getTarget())){
			if(network.getTarget() != null)
				target = new String(network.getTarget());
			else 
				target = null;
			when_my_tick = when;
			retour = true;
		}
		if(target == null && network.getTarget() != null){
			target = new String(network.getTarget());
			when_my_tick = when;
			retour = true;
		}
		if(position.equals(network.getPosition())){
			position = new String(network.getPosition());
			when_my_tick = when;
			retour = true;
		}
		if(free_space != network.getFreeSpace()){
			free_space = network.getFreeSpace();
			when_my_tick = when;
			retour = true;
		}
		if(total_space != network.getTotalSpace()){
			total_space = network.getTotalSpace();
			when_my_tick = when;
			retour = true;
		}
		return retour;
	}
	
	
	public AID getId(){
		return id;
	}
	
	public int getWhen() {
		return when_my_tick;
	}
	public int getFreeSpace() {
		return free_space;
	}
	
	public int getTotalSpace() {
		return total_space;
	}
	
	public int getHisWhen() {
		return when_his_tick;
	}
	
	public String getPosition() {
		return position;
	}
	
	public AgentMode getAgentMode(){
		return mode;
	}
	
	public String getTarget(){
		return target;
	}
	
	public void update(DummyExploAgent me){
		this.when_his_tick = me.getWhen();
		this.when_my_tick = me.getWhen();
		this.position = me.getCurrentPosition();
		this.free_space = me.getBackPackFreeSpace();
		this.when_my_tick = me.getWhen();
	}

	
}
