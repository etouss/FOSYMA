package mas.structuregraph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Room implements Serializable {
	/*Serial ID*/
	private static final long serialVersionUID = 1L;
	
	/*Stuff that go through network*/
	
	/*Id*/
	private String id;
	/*TimeStamp to determine the time of the info*/
	private int timestamp = 0;
	/*Boolean to know whether we have visited it*/
	private boolean visited = false;
	/*The amount of treasure in that position*/
	private int treasure_value = -1;
	/*Linked Room*/
	private HashSet<Room> linked_rooms = new HashSet<Room>();
	
	
	/*Stuff that doesn't go through network*/
	/*Useful for intern algorithm */
	private transient Castle castle = null;
	private transient boolean updated = false;
	private transient int when = -1;
	//public transient float page_ranking = 1;
	//public transient float prev_page_ranking = 1;
	private transient long hash = 0;
	
	
<<<<<<< HEAD
	
=======
>>>>>>> a2fd16af85a169ec6d36edafb6f6a7d6cb0535e6
	public void hash_room(){
		long hash = id.hashCode()+treasure_value;
		for(Room r_linked : linked_rooms){
			hash += r_linked.getId().hashCode();
		}
		this.hash=hash;
	}
	
	public long get_hash(){
		return hash;
	}
	
	/*public void update_page_ranking(){
		page_ranking = isVisited()?0:1;
		for(Room r_linked : linked_rooms){
			page_ranking += r_linked.prev_page_ranking / linked_rooms.size();
		}
		
	}
	public void equalize_page_ranking(){
		prev_page_ranking = page_ranking;
	}*/
	
	public void add_link(Room link_room,int when_called){
		if(!this.linked_rooms.contains(link_room)){
			this.linked_rooms.add(link_room);
			this.when = when_called;
			castle.set_last_update(when_called);
		}
	}
	
	public String getId(){
		return this.id;
	}
	
	public boolean isVisited(){
		return this.visited;
	}
	
	public int get_treasure_value(){
		return this.treasure_value;
	}
	
	public void set_treasure_value(int value){
		treasure_value = value;
	}
	
	public void setVisited(int when){
		if(!visited){
			visited = true;
			this.when = when;
			castle.set_last_update(when);
			//page_ranking -= 1;
			//prev_page_ranking -= 1;
			castle.setVisited(this);
		}
	}
	
	public HashSet<Room> getLinkedRooms(){
		return linked_rooms;
	}
	
	public void setLinkedRooms_network(HashSet<Room> to_send){
		HashSet<Room> new_linkeds_rooms = new HashSet<Room>();
		for(Room r_linked : this.linked_rooms){
			for(Room r_to_send : to_send){
				if(r_to_send.getId().equals(r_linked.getId())){
					new_linkeds_rooms.add(r_to_send);
					break;
				}
			}
		}
		this.linked_rooms = new_linkeds_rooms;
	}
	
	public int getWhen(){
		return when;
	}
	
	public Room(String id,Castle castle,int when){
		this.id = new String(id);
		this.castle = castle;
		this.when = when;
		castle.set_last_update(when);
	}
	
	public Room(Room room_to_copy){
		this.id = room_to_copy.getId();
		this.visited = room_to_copy.isVisited();
		this.linked_rooms = new HashSet<Room>(room_to_copy.getLinkedRooms());
		
		
		/*Copy the rest*/
	}
	
	public void update_room(Room network_room,int when_receive){
		if(this.updated) 
			return;
		this.updated = true;
		/*Update this room*/
		//boolean page_rank_reset_needed = false;
		if(network_room.isVisited() ){
			this.setVisited(when_receive);
		}
		/*If new_info update this.when*/
		
		
		/*Update linked_rooms*/
		for(Room network_linked_room :network_room.getLinkedRooms()){
			Room linked_room = castle.get_room(network_linked_room.getId(),when_receive);
			castle.add_link(this, linked_room,when_receive);
			linked_room.update_room(network_linked_room,when_receive);
		}
	}

	
	public void raz(){
		this.updated = false;
		hash_room();
<<<<<<< HEAD
	}
	
	public double get_reward(double nb_unvisited,int step,int coef){
		/*prop au truc inexploré*/
		return nb_unvisited/((step+1)*coef*this.getLinkedRooms().size());
	}
	
=======
	}
	
	public double get_reward(double nb_unvisited,int step,int coef){
		/*prop au truc inexploré*/
		return nb_unvisited/((step+1)*coef*this.getLinkedRooms().size());
	}
	
>>>>>>> a2fd16af85a169ec6d36edafb6f6a7d6cb0535e6
	public int coef_occupied(int last_coef, Integer when_checked, int when){
		int deviance = 50;
		if(when_checked == null){
			return last_coef;
		}
		//System.out.println(when-when_checked);
		return last_coef*deviance/Math.min(deviance,when-when_checked);
	}
	
	public double reward(HashMap<Room,Integer> occupied,int when_asked){
		HashSet<Room> already_visited = new HashSet<Room>();
		LinkedList<Room> room_to_check = new LinkedList<Room>();
		LinkedList<Integer> step_to_go = new LinkedList<Integer>();
		LinkedList<Integer> coeficient_occupy = new LinkedList<Integer>();
		double result = isVisited()?0:1;
		/*double nb_unvisited_1 = 0;
		for(Room r_linked : linked_rooms){
			//if(occupied.contains(r_linked)) continue;
			nb_unvisited_1 += r_linked.isVisited()?0:1;
			//result += r_linked.isVisited()?0:(1.0/(1*linked_rooms.size()));
			room_to_check.add(r_linked);
			step_to_go.add(1);
			already_visited.add(r_linked);
		}
		result += this.get_reward(nb_unvisited_1,0);*/
		room_to_check.add(this);
		step_to_go.add(0);
		coeficient_occupy.add(1);
		already_visited.add(this);
		while(!room_to_check.isEmpty()){
			Room to_do = room_to_check.removeFirst();
			int step = step_to_go.removeFirst();
			int coef = coeficient_occupy.removeFirst();
			double nb_unvisited = 0;
			for(Room r_linked : to_do.getLinkedRooms()){
				//if(occupied.contains(r_linked)) continue;
				if(!already_visited.contains(r_linked)){
					nb_unvisited += r_linked.isVisited()?0:1;
					room_to_check.add(r_linked);
					step_to_go.add(step+1);
					coeficient_occupy.add(coef_occupied(coef,occupied.get(r_linked),when_asked));
					already_visited.add(r_linked);
				}
			}
			result += to_do.get_reward(nb_unvisited, step,coef);
		}
		//System.out.println(result);
		return result;
	}

}
