package mas.data;

import java.util.HashMap;

import jade.core.AID;
import mas.agents.AgentInfo;
import mas.agents.DummyExploAgent;

public class Data {
	
	/*Donné de réglagle*/
	//private static final int nb_tick_maximal = 0;
	
	/*Donnée fixé*/
	private DummyExploAgent me;
	private HashMap<AID,AgentInfo> agents;
	private int nb_agent;

	
	/*Donné potentiellement calculé et probabilisé*/
	
	
	
	
	public Data(DummyExploAgent me,int nb_agent){
		/*
		 * nb_agent = ?
		 * */
		this.nb_agent = nb_agent;
		this.me = me;
		this.agents = me.getAgents();
	}
	
	
	
	
	
	
	
	
	/*Moyenne des sac connue * nb_agent*/
	public int total_back_pack;
	public int total_back_pack_free;
	public int mean_back_pack_free;
	public int best_pack_prob;
	public double knowledge_validity;
	
	
	public void update_value(int when){
		/*Moyenne sur les sacs connues*/
		int total_back = 0;
		for(AgentInfo ainfo: agents.values()){
			total_back += ainfo.getTotalSac();
		}
		total_back_pack = total_back/agents.size()*nb_agent;
		/*Moyenne + conjecture sur les sac connue*/
		int total_prop = 0;
		int best_pack = 0;
		for(AgentInfo ainfo: agents.values()){
			/*On calcul la proportion de free a l'instant t ou l'on connaisait ainfo*/
			int prop_free = ainfo.getFreeSac()*100/ainfo.getTotalSac();
			/*On conjecture un comportement en 100/1+ax ce qui est faux*/
			/*On considere des tick a peu pret uniforme ce qui est faux aussi*/
			
			/*Determinons a*/
			int n_prop = 0;
			if(prop_free == 100) {
				n_prop = 100;
			}
			else if(prop_free != 0){
				int a = (100-prop_free)/(prop_free*ainfo.getWhen());
				n_prop = 100/(1+a*(ainfo.getWhen()+ainfo.getDelta(when)));
			}
			total_prop += n_prop;
			best_pack = Math.max(best_pack,n_prop*ainfo.getTotalSac());
		}
		total_prop = total_prop/agents.size();
		total_back_pack_free = (total_prop * total_back_pack) /100;
		mean_back_pack_free = total_back_pack_free/agents.size();
		best_pack_prob = best_pack/100;
		
		knowledge_validity = 0;
		for(AgentInfo ainfo: agents.values()){
			knowledge_validity += (1.0/nb_agent)*((double)(when-ainfo.getDelta(when))/(double)when);
			//System.out.println(((when-ainfo.getDelta(when))/when));
		}
		
		
		/*System.out.println("Agent: "+me.getAID());
		System.out.println("Agents: "+agents.size());
		System.out.println("My"+me.getBackPackFreeSpace());
		System.out.println("MyMax"+me.totalbackpack);
		System.out.println("Tot: "+total_back_pack);
		System.out.println("Free: "+total_back_pack_free);
		System.out.println("Best: "+best_pack_prob);
		System.out.println("Know: "+knowledge_validity);
		*/
		
	}
}
