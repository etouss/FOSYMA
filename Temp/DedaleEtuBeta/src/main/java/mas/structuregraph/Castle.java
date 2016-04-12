package mas.structuregraph;

import java.util.HashMap;
import java.util.HashSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import jade.core.AID;

import java.util.LinkedList;

//import mas.agents.CopyOfDummyExploAgent;
import mas.agents.DummyExploAgent;

public class Castle {
	/*Every room known in the castle*/
	private HashMap<String,Room> rooms = new HashMap<String,Room>();
	/*Useful for graphic representation*/
	private Graph gstream;
	/*Whether if we have visited every room or not*/
	private boolean done_visited = false;
	/*Try to guess where are each agent*/
	private HashMap<AID,String> agents_position;
	private HashMap<AID,Integer> agents_position_probability;
	
	private String last_id = "";
	
	private int last_update = -1;
	
	private int nb_test = 0;
	
	public boolean have_to_send(Integer when){
		//bourin_update();
		if(when == null){
			nb_test ++;
			System.out.println("SERIOUS?"+ nb_test);
			return true;
		}
		return last_update > when;
	}
	
	public String toString(){
		String casStr = null;
		for ( Room room: rooms.values() ){
			casStr += room.toString() + "    ";
		}
		return casStr;
	}
	
	public void bourin_update(){
		for(Room potential_room : rooms.values()){
			last_update = Math.max(last_update, potential_room.getWhen());
		}
	}
	
	public void set_last_update(int when){
		this.last_update = when;
	}
	
	public HashMap<Room,Integer> get_occupied_room(){
		//int deviance = 20;
		HashMap<Room,Integer> result = new HashMap<Room,Integer>();
		for(AID agent : agents_position_probability.keySet()){
			Room r = this.get_room(agents_position.get(agent),-1);
			int prob = this.agents_position_probability.get(agent);
			if(result.get(r) == null || result.get(r) < prob){
				result.remove(r);
				result.put(r,prob);
			}
		}
		return result;
	}
	
	public boolean is_visited(String id){
		Room unknow = rooms.get(id);
		if(unknow == null){
			return false;
		}
		else{
			return unknow.isVisited();
		}
	}
	
	public boolean is_done_visited(){
		if(done_visited)
			return done_visited;
		for(Room any_room : rooms.values()){
			if(!any_room.isVisited()){
				return false;
			}
		}
		done_visited = true;
		return done_visited;
	}
	
	public Room get_room(String id){
		Room the_room = rooms.get(id);
		if(the_room == null){
			return null;
		}
		return the_room;
	}
	
	public Room get_room(String id,int when){
		Room the_room = rooms.get(id);
		if(the_room == null){
			the_room = new Room(id,this,when);
			this.add_room(the_room);
		}
		return the_room;
	}
	
	public void add_room(Room room){
		if(!this.rooms.containsKey(room.getId()))
			this.rooms.put(room.getId(), room);
		if(this.gstream.getNode(room.getId())==null)
			this.gstream.addNode(room.getId());
	}
	
	public void add_link(Room roomA,Room roomB,int when){
		roomA.add_link(roomB,when);
		roomB.add_link(roomA,when);
		if(gstream.getEdge(roomA.getId()+roomB.getId()) == null && gstream.getEdge(roomB.getId()+roomA.getId()) == null)
			gstream.addEdge(roomA.getId()+roomB.getId(), roomA.getId(), roomB.getId());
	}
	
	public void setVisited(Room r){
		if (r.get_treasure_value() > 0)
			gstream.getNode(r.getId()).setAttribute("ui.class", "treasure");
		else
			gstream.getNode(r.getId()).setAttribute("ui.class", "visited");
	}
	

	
	public void setThere(Room r){
		Room room = get_room(last_id);
		if (room.get_treasure_value() > 0)
			gstream.getNode(last_id).setAttribute("ui.class", "treasure");
		else
			gstream.getNode(last_id).setAttribute("ui.class", "visited");
		gstream.getNode(r.getId()).setAttribute("ui.class", "there");
		last_id = r.getId();
	}
	

	
	
	public Castle(DummyExploAgent agent,HashMap<AID,String> agents_position,HashMap<AID,Integer> agents_position_probability){
		String nodeStyle_visited="node.visited {"+"fill-color: green;"+"}";
		String nodeStyle_treasure="node.treasure {"+"fill-color: yellow;"+"}";
		String nodeStyle_position="node.there {"+"fill-color: red;"+"}";
		String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
		String nodeStyle=defaultNodeStyle+nodeStyle_visited+nodeStyle_treasure+nodeStyle_position;
		this.gstream = new SingleGraph(agent.getLocalName());
		gstream.setAttribute("ui.stylesheet",nodeStyle);
		gstream.setAttribute("ui.title",agent.getLocalName());
		gstream.display();
		Room first_room = new Room(agent.getCurrentPosition(),this,agent.getWhen());
		this.add_room(first_room);
		this.agents_position = agents_position;
		this.agents_position_probability = agents_position_probability;
		last_id = first_room.getId();
		this.setThere(first_room);
		//this.sources.add(first_room);
	}
	/*
	public Castle(CopyOfDummyExploAgent agent, HashMap<AID, String> agents_position, HashMap<AID, Integer> agents_position_probability2) {
			String nodeStyle_visited="node.visited {"+"fill-color: green;"+"}";
			String nodeStyle_treasure="node.treasure {"+"fill-color: yellow;"+"}";
			String nodeStyle_position="node.there {"+"fill-color: red;"+"}";
			String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
			String nodeStyle=defaultNodeStyle+nodeStyle_visited+nodeStyle_treasure+nodeStyle_position;
			this.gstream = new SingleGraph(agent.getLocalName());
			gstream.setAttribute("ui.stylesheet",nodeStyle);
			gstream.setAttribute("ui.title",agent.getLocalName());
			gstream.display();
			Room first_room = new Room(agent.getCurrentPosition(),this,agent.getWhen());
			this.add_room(first_room);
			this.agents_position = agents_position;
			this.agents_position_probability = agents_position_probability;
			last_id = first_room.getId();
			this.setThere(first_room);
	}
	
	*/

	public HashSet<Room> room_to_send(int when_last_send){
		/*Determine which room have to be sent*/
		HashSet<Room> to_send = new HashSet<Room>();
		for(Room potential_room : rooms.values()){
			if(potential_room.getWhen() > when_last_send || potential_room.get_treasure_value() > 0)
				/*Add a copy of the room*/
				to_send.add(new Room(potential_room));
		}
		
		/*Modify the copy to only send a fair amount of linked_rooms*/
		if(!to_send.isEmpty()){
			//System.out.print("Send: ");
			for(Room r_to_send : to_send){
				r_to_send.setLinkedRooms_network(to_send);
				//System.out.print("["+r_to_send.getId()+"] ");
			}
			//System.out.print("\n");
			
		}
		return to_send;
	}
	
	public void update_castle(HashSet<Room> rooms_receive,int when_receive){
		//boolean page_rank_reset_needed = false;
		for(Room room_receive: rooms_receive){
			Room room_concerne = this.get_room(room_receive.getId(), when_receive);
			room_concerne.update_room(room_receive, when_receive);
			System.out.println("update  " + room_concerne.toString() );
		}
		this.raz();
		//if(page_rank_reset_needed)
		//	page_ranking_reset();
	}
	
	public void raz(){
		for(Room any_room : rooms.values()){
			any_room.raz();
		}
	}
	
	
	public String where_to_to_heavy(String position,int when){
		int deviance = 10;
		HashMap<Room,Integer> occupied = get_occupied_room();
		double max_reward = 0;
		String result = "";
		Room there = this.get_room(position, -1);
		for(Room r_linked : there.getLinkedRooms()){
			double reward = -1;
			Integer prob = occupied.get(r_linked);
			if(prob == null || prob < when - deviance)
				reward = r_linked.reward(occupied,when);
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
			//System.out.println(reward);
		}	
		//System.out.println(max_reward);
		return result;
	}
	
	public long hash_castle(){
		long hash = 0;
		for(Room r : rooms.values()){
			hash += r.get_hash(); 
		}
		return hash;
	}
	
	/*
	public String where_to_to_page(String position,int when){
		float max_reward = -1;
		String result = "";
		Room there = this.get_room(position, -1);
		for(Room r_linked : there.getLinkedRooms()){
			float reward = r_linked.page_ranking;
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
		}
		System.out.println("Go : "+result);
		return result;
	}
	
	public void page_ranking_soft(){
		for(Room any_room : rooms.values()){
			any_room.update_page_ranking();
		}
		for(Room any_room : rooms.values()){
			any_room.equalize_page_ranking();
		}
	}
	
	public void page_ranking_reset(){
		int nb_iteration = 5;
		for(Room any_room : rooms.values()){
			any_room.page_ranking = any_room.isVisited()?0:1;
			any_room.prev_page_ranking = any_room.isVisited()?0:1;
		}
		for(int i = 0; i < nb_iteration;i ++){
			for(Room any_room : rooms.values()){
				any_room.update_page_ranking();
			}
			for(Room any_room : rooms.values()){
				any_room.equalize_page_ranking();
			}
		}
	}
	*/
	
}
