package mas.structuregraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Noeud implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private ArrayList<Noeud> fils;
	/*Pour comparer la véracité des propos*/
	private int timestamp; 
	private transient int tick; 
	private boolean been_there;
	private transient boolean synchroniser;
	private transient int stamp;
	private transient MyGraph graph;
	
	public Noeud(String id,MyGraph graph,int tick){
		this.id = id;
		this.fils = new ArrayList<Noeud>();
		this.synchroniser = false;
		this.graph = graph;
		this.been_there = false;
		this.tick = tick;
	}
	
	public void setTick(int ti){
		this.tick = ti;
	}
	
	
	/*All méthode*/
	/*La classe noeud doit être capable de ce compléter elle même avec un autre noeud*/
	public boolean synchro(Noeud node,int tick_true){
		if(synchroniser){
			return true;
		}
		if(!node.getId().equals(this.getId())){
			return false;
		}
		else{
			synchroniser = true;
			if(node.has_been_there()) this.there();
		}
		
		
		for(Noeud node_node: node.getFils()){
			boolean known_node = false;
			for(Noeud node_this:this.getFils()){
				if(node_this.getId().equals(node_node.getId())){
					node_this.synchro(node_node,tick_true);
					known_node = true;
					break;
				}
			}
			if(!known_node){
				/*Add new node to fils + synchro propagation*/
				/*Attention c'est la que c'est compliqué et marrant*/
				Noeud new_node = null;
				try {
					new_node = graph.return_node(node_node.getId());
					graph.add_edge(this, new_node,tick_true);
				} catch (Node_not_known_Exeption e) {
					/*Est qu'on lui ajoute déja un fils ?*/
					new_node = new Noeud(node_node.getId(),graph,tick_true);
					graph.add_node(new_node);
					graph.add_edge(this, new_node,tick_true);
				}
				/*On repasse par la méthode synchro car ca gere tout*/
				new_node.synchro(node_node,tick_true);
			}
		}
		
		return true;
	}
	
	public String getId(){
		return id;
	}
	
	public boolean has_been_there(){
		return been_there;
	}
	
	public void there(){
		been_there = true;
	}
	
	public List<Noeud> getFils(){
		return fils;
	}
	
	
	public void synchro_reset(){
		synchroniser = false;
	}
	
	public void ticked_send_multiple(ArrayList<Noeud> result, int tick){
		for(Noeud n : this.fils){
			result.add(n.ticked_send_unique(result,tick));
		}
	}
	
	public Noeud ticked_send_unique(ArrayList<Noeud> result, int tick){
		if(this.tick > tick){
			/*je m'envoie*/
			Noeud me = new Noeud(this.id,this.graph,this.tick);
			for(Noeud n : this.fils){
				me.getFils().add(n.ticked_send_unique(result, tick));
			}
		return me;
		}
		else{
			ticked_send_multiple(result,tick);
		}
		return null;
	}
	
	/*Methode 2*/
	/*La classe noeud doit être capable de connaitre ces noeud incomplet (les noeud dont elle ne connait pas la suite)*/
	

}
