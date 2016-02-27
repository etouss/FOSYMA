package mas.structuregraph;

import java.io.Serializable;
import java.util.HashSet;

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
	
	public void add_link(Room link_room,int when_called){
		if(!this.linked_rooms.contains(link_room)){
			this.linked_rooms.add(link_room);
			this.when = when_called;
		}
	}
	
	public String getId(){
		return this.id;
	}
	
	public boolean isVisited(){
		return this.visited;
	}
	
	public void setVisited(int when){
		if(!visited){
			visited = true;
			this.when = when;
		}	
	}
	
	public HashSet<Room> getLinkedRooms(){
		return linked_rooms;
	}
	
	public void setLinkedRooms_network(HashSet<Room> to_send){
		HashSet<Room> new_linkeds_rooms = new HashSet<Room>();
		for(Room r_to_send : to_send){
			for(Room r_linked : this.linked_rooms){
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
	}
	
	public Room(Room room_to_copy){
		this.id = room_to_copy.getId();
		this.visited = room_to_copy.isVisited();
		this.linked_rooms = new HashSet<Room>(room_to_copy.getLinkedRooms());
		/*Copy the rest*/
	}
	
	public void update_room(Room network_room,int when_receive){
		if(this.updated) 
			return ;
		this.updated = true;
		/*Update this room*/
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
	}
	

}
