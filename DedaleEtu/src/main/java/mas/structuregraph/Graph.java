package mas.structuregraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Graph {
	
	private HashMap<String,Noeud> nodes;
	/*List si pluisieur SCC*/
	private List<Noeud> sources;
	
	public Noeud return_node(String id) throws Node_not_known_Exeption{
		if(!nodes.containsKey(id)) throw new Node_not_known_Exeption();
		return nodes.get(id);
	}
	
	public void add_node(Noeud node){
		nodes.put(node.getId(),node);
	}
	
	public void update_scc(){
		sources = new ArrayList<Noeud>();
		LinkedList<Noeud> copy= new LinkedList<Noeud>();
		copy.addAll(nodes.values());
		while(!copy.isEmpty()){
			Noeud current = copy.removeFirst();
			sources.add(current);
			/*remove from copy*/
			remove_node_from_copy(copy,current);
		}
	}
	
	public void remove_node_from_copy(LinkedList<Noeud> copy,Noeud node){
		for(Noeud node_new : node.getFils()){
			if(copy.contains(node_new)){
				copy.remove(node_new);
				remove_node_from_copy(copy,node_new);
			}	
		}
	}
	
	
	public void synchro(List<Noeud> nodes){
		/*Ne pas oublier de remettre les boolean synchro a 0*/
		for(Noeud node : nodes){
			Noeud my_node = null;
			try {
				my_node = return_node(node.getId());
			} catch (Node_not_known_Exeption e) {
				my_node = new Noeud(node.getId(), node.getGraph(), 0);
				add_node(node);
			}
			my_node.synchro(node, node.getTick());
		}
		
		
		
	}
	
	public int hash_code(){
		/*Utile pour vérifier que l'on est bien d'accord sur la map*/
		/*Doit hasher toute les information de la hashmap (on considere que les liens seront toujours bon ?)*/
		return 0;
	}
}
