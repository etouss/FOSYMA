package mas.structuregraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;


import mas.abstractAgent;

public class MyGraph {
	
	private HashMap<String,Noeud> nodes;
	/*List si pluisieur SCC*/
	private ArrayList<Noeud> sources;
	private Graph gstream;
	
	public MyGraph(abstractAgent agent){
		gstream = new SingleGraph(agent.getLocalName());
		Noeud node = new Noeud(agent.getCurrentPosition(),this,0);
		nodes = new HashMap<String,Noeud>();
		sources = new ArrayList<Noeud>();
		add_node(node);
		sources.add(node);
	}
	
	public Noeud return_node(String id) throws Node_not_known_Exeption{
		if(!nodes.containsKey(id)) throw new Node_not_known_Exeption();
		return nodes.get(id);
	}
	
	public boolean already_known(String id){
		if(nodes.containsKey(id)){
			return nodes.get(id).has_been_there();
		}
		return false;
	}
	
	public ArrayList<Noeud> getSources(){
		return sources;
	}
	
	public void add_node(Noeud node){
		gstream_add_node(node);
		nodes.put(node.getId(),node);
	}
	
	public void update_scc(){
		sources = new ArrayList<Noeud>();
		LinkedList<Noeud> copy= new LinkedList<Noeud>();
		copy.addAll(nodes.values());
		while(!copy.isEmpty()){
			Noeud current = copy.removeFirst();
			sources.add(current);
			current.synchro_reset();
			/*remove from copy*/
			remove_node_from_copy(copy,current);
		}
	}
	
	public void remove_node_from_copy(LinkedList<Noeud> copy,Noeud node){
		for(Noeud node_new : node.getFils()){
			if(copy.contains(node_new)){
				node_new.synchro_reset();
				copy.remove(node_new);
				remove_node_from_copy(copy,node_new);
			}	
		}
	}
	
	
	public void synchro(List<Noeud> nodes,int tick){
		/*Ne pas oublier de remettre les boolean synchro a 0*/
		
		for(Noeud node : nodes){
			if (node != null){
				Noeud my_node = null;
				try {
					my_node = return_node(node.getId());
				} catch (Node_not_known_Exeption e) {
					my_node = new Noeud(node.getId(),this,tick);
					add_node(node);
				}
				my_node.synchro(node,tick);
			
			update_scc();
			}
		}
	}
	
	
	public int hash_code(){
		/*Utile pour vérifier que l'on est bien d'accord sur la map*/
		/*Doit hasher toute les information de la hashmap (on considere que les liens seront toujours bon ?)*/
		return 0;
	}
	
	public String toString(){
		String result = "";
		for(String id : nodes.keySet()){
			result += "["+id+"],";
		}
		return result;
	}
	
	public void add_edge(Noeud node1, Noeud node2,int tick){
		if(!node1.getFils().contains(node2)){
			node1.getFils().add(node2);
			node1.setTick(tick);
		}
		if(!node2.getFils().contains(node1)){
			node2.getFils().add(node1);
			node2.setTick(tick);
		}
		gstream_add_edge(node1,node2);
	}
	
	public void gstream_add_node(Noeud node){
		gstream.addNode(node.getId());
	}
	
	public void gstream_add_edge(Noeud node1,Noeud node2){
		if(gstream.getEdge(node2.getId()+node1.getId()) == null && gstream.getEdge(node1.getId()+node2.getId()) == null){
			gstream.addEdge(node1.getId()+node2.getId(), node1.getId(), node2.getId());
			//System.out.println("HEY");
		}
	}
	
	public void display(){
		gstream.display();
	}
	
	public ArrayList<Noeud> ticked_send(int tick){
		ArrayList<Noeud> result = new ArrayList<Noeud>();
		for(Noeud source : sources){
			result.add(source.ticked_send_unique(result,tick));
		}
		return result;
	}
	
}
