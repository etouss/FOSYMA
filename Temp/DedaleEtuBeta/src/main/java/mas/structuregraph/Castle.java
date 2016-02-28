package mas.structuregraph;

import java.util.HashMap;
import java.util.HashSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import jade.core.AID;

import java.util.LinkedList;
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
	
	public HashSet<Room> get_occupied_room(int when){
		int deviance = 20;
		HashSet<Room> result = new HashSet<Room>();
		for(AID agent : agents_position_probability.keySet()){
			if(agents_position_probability.get(agent)+deviance > when){
				result.add(this.get_room(agents_position.get(agent),-1));
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
		gstream.getNode(r.getId()).setAttribute("ui.class", "visited");
	}
	
	
	public Castle(DummyExploAgent agent,HashMap<AID,String> agents_position,HashMap<AID,Integer> agents_position_probability){
		String nodeStyle_treasure="node.visited {"+"fill-color: green;"+"}";
		String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
		String nodeStyle=defaultNodeStyle+nodeStyle_treasure;
		this.gstream = new SingleGraph(agent.getLocalName());
		gstream.setAttribute("ui.stylesheet",nodeStyle);
		gstream.display();
		Room first_room = new Room(agent.getCurrentPosition(),this,agent.getWhen());
		this.add_room(first_room);
		this.agents_position = agents_position;
		this.agents_position_probability = agents_position_probability;
		//this.sources.add(first_room);
	}
	
	public HashSet<Room> room_to_send(int when_last_send){
		/*Determine which room have to be sent*/
		HashSet<Room> to_send = new HashSet<Room>();
		for(Room potential_room : rooms.values()){
			if(potential_room.getWhen() > when_last_send)
				/*Add a copy of the room*/
				to_send.add(new Room(potential_room));
		}
		
		/*Modify the copy to only send a fair amount of linked_rooms*/
		/*if(!to_send.isEmpty()){
			System.out.print("Send: ");
			for(Room r_to_send : to_send){
				r_to_send.setLinkedRooms_network(to_send);
				System.out.print("["+r_to_send.getId()+"] ");
			}
			System.out.print("\n");
			
		}*/
		return to_send;
	}
	
	public void update_castle(HashSet<Room> rooms_receive,int when_receive){
		boolean page_rank_reset_needed = false;
		for(Room room_receive: rooms_receive){
			Room room_concerne = this.get_room(room_receive.getId(), when_receive);
			page_rank_reset_needed = room_concerne.update_room(room_receive, when_receive) | page_rank_reset_needed;
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
		HashSet<Room> occupied = get_occupied_room(when);
		double max_reward = 0;
		String result = "";
		Room there = this.get_room(position, -1);
		for(Room r_linked : there.getLinkedRooms()){
			double reward = r_linked.reward(occupied);
			if(reward > max_reward){
				max_reward = reward;
				result = r_linked.getId();
			}
		}	
		return result;
	}
	
	
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
		//System.out.println("New visited");
		/*
		for(Room any_room : rooms.values()){
			if(any_room.isVisited())
				System.out.println(any_room.getId()+" visited :"+any_room.page_ranking);
			else
				System.out.println(any_room.getId()+" unvisited :"+any_room.page_ranking);
		}
		*/
	}
	
	
}
