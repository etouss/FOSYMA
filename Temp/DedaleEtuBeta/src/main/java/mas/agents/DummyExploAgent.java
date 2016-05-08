package mas.agents;


import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashMap;
import java.util.HashSet;

import mas.abstractAgent;
import mas.behaviour.Walk;
import mas.behavioursCom.ReceiveConfirm;
import mas.behavioursCom.ReceiveInform;
import mas.behavioursCom.ReceiveRequest;
import mas.behavioursCom.SayConfirm;
import mas.behavioursCom.SayInform;
import mas.behavioursCom.SayRequest;
import mas.data.Data;
import mas.structuregraph.Castle;
import mas.structuregraph.Room;
import env.Environment;

public class DummyExploAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;
	private Castle castle;
	private int when= 0;
	private AgentMode mode = AgentMode.EXPLORER;
	private String target = null;
	private HashMap<AID,AgentInfo> agents;
	private Data data;
	private int totalbackpack;
	private AgentDescisionMaker dm;
	private int last_update_agent;
	
	/*Safe MODE*/
	private int leader_num = -1;
	private AID leader_id = null;
	private HashMap<AID,HashSet<String>> contrainte;
	//private boolean done = false;
	
	public void add_contrainte(AID id, HashSet<String> target){
		contrainte.remove(id);
		contrainte.put(id,target);
	}
	
	public HashMap<AID,HashSet<String>> getContrainte(){
		return contrainte;
	}

	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}
		
		
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription();
		sd.setType( "explorer" ); 
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd );
		} catch (FIPAException fe) 
			{
			fe.printStackTrace(); 
		}
		

		
		//Add the behaviours
		this.agents = new HashMap<AID,AgentInfo>();
		this.castle = new Castle(this,agents);
		//this.graph.display();
		//addBehaviour(new SayHello(this));
		//sd.setType( "explorer" ); 
		//dfd.addServices(sd);
		
		ServiceDescription sdsearch = new ServiceDescription();
		sdsearch.setType( "explorer" ); 
		DFAgentDescription dfdsearch = new DFAgentDescription();
		dfdsearch.addServices(sdsearch);
		
		DFAgentDescription[] result = null;
		try {
			Thread.sleep(1000);
			result = DFService.search(this, dfdsearch);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result.length + " results" );
		if (result.length>0){
			for(DFAgentDescription a : result){
				System.out.println(" " + a.getName()+ "\n" );
			}
		}
		HashSet<AID> lsend = new HashSet<AID>();
		HashSet<AID> lack = new HashSet<AID>();
		HashMap<AID,Integer> hmsend = new HashMap<AID,Integer>();
		HashMap<AID,Integer> hmack = new HashMap<AID,Integer>();
		HashMap<AID,Long> hmcode = new HashMap<AID,Long>();
		AgentLock un_move = new AgentLock(this.getAID());
		
		
		addBehaviour(new SayRequest(this, result,castle,un_move));
		addBehaviour(new ReceiveRequest(this,castle,hmack,agents,un_move));
		addBehaviour(new SayInform(this,castle, lsend,hmsend,hmack,hmcode,un_move));
		addBehaviour(new ReceiveInform(this,castle,lack,agents,un_move));
		addBehaviour(new SayConfirm(this,lack,castle,un_move));
		addBehaviour(new ReceiveConfirm(this,lsend,hmsend,hmack,agents,hmcode,un_move));
		addBehaviour(new Walk(this,castle,lsend,lack,un_move));

		System.out.println("the agent "+this.getLocalName()+ " is started");
		totalbackpack = this.getBackPackFreeSpace();
		//data = new Data(this,result.length);
		//dm = new AgentDescisionMaker(this);

	}
	
	public void incWhen(){
		//agents.remove(this.getAID());
		//AgentInfo ai = new AgentInfo(this,0);
		//ai.setWhen(when+1);
		//agents.put(getAID(), ai);
		this.when++;
		//data.update_value(this.when);
	}
	
	public int getWhen(){
		return when;
	}
	
	public Castle getCastle(){
		return this.castle;
	}
	
	public HashMap<AID,AgentInfo> getAgents(){
		return this.agents;
	}
	
	public AgentMode getMode(){
		return this.mode;
	}
	
	public String getTarget(){
		return this.target;
	}
	
	/*public void setMode(AgentMode mode){
		this.mode = mode;
	}*/
	
	public void setSafe(int leader_num,AID leader){
		this.mode = AgentMode.SAFE;
		this.leader_num = leader_num;
		this.leader_id = leader;
		contrainte = new HashMap<AID,HashSet<String>>();
	}
	
	public void setExplo(){
		this.mode = AgentMode.EXPLORER;
		this.leader_num = -1;
		this.leader_id = null;
		contrainte = null;
		target = null;
	}
	
	
	public Data getData(){
		return data;
	}

	
	public void dm(Room r){
		dm.on_treasure(r);
	}
	
	public int get_totalbackpack(){
		return this.totalbackpack;
	}
	
	public int get_leader_num(){
		return this.leader_num;
	}
	
	public void set_leader_num(int leader_num){
		this.leader_num = leader_num;
	}
	
	public void set_leader(AID leader){
		this.leader_id = leader;
	}
	
	public AID get_leader(){
		return leader_id;
	}
	
	public HashSet<AgentInfo> agent_to_send(int when, AID to_who){
		HashSet<AgentInfo> result = new HashSet<AgentInfo>();
		for(AgentInfo info : agents.values()){
			if(!to_who.equals(info.getId()) && info.getWhen_my_tick()>when){
				result.add(info);
			}
		}
		return result;
	}
	
	public void update_info(HashSet<AgentInfo> infos_recu,int when){
		for(AgentInfo a_info_recu : infos_recu){
			if(!agents.containsKey(a_info_recu.getId())){
				a_info_recu.setWhen(when);
				agents.put(a_info_recu.getId(), a_info_recu);
				this.last_update_agent = when;
				continue;
			}
			AgentInfo known_info = agents.get(a_info_recu.getId());
			if(known_info.getWhen() < a_info_recu.getWhen()){
				a_info_recu.setWhen(when);
				agents.remove(a_info_recu.getId());
				agents.put(a_info_recu.getId(), a_info_recu);
				this.last_update_agent = when;
			}
			
		}
	}
	
	public boolean have_to_send(int when){
		return when < last_update_agent;
	}
	
	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
}
