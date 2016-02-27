package mas.structuregraph;

import java.util.HashMap;
import java.util.HashSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import mas.agents.DummyExploAgent;

public class Castle {
	/*Every room known in the castle*/
	private HashMap<String,Room> rooms = new HashMap<String,Room>();
	/*Useful for graphic representation*/
	private Graph gstream;
	/*Whether if we have visited every room or not*/
	private boolean done_visited = false;
	
	
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
	
	
	public Castle(DummyExploAgent agent){
		this.gstream = new SingleGraph(agent.getLocalName());
		gstream.display();
		Room first_room = new Room(agent.getCurrentPosition(),this,agent.getWhen());
		this.add_room(first_room);
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
		System.out.print("Send: ");
		for(Room r_to_send : to_send){
			r_to_send.setLinkedRooms_network(to_send);
			System.out.print("["+r_to_send.getId()+"] ");
		}
		System.out.print("\n");
		return to_send;
	}
	
	public void update_castle(HashSet<Room> rooms_receive,int when_receive){
		for(Room room_receive: rooms_receive){
			Room room_concerne = this.get_room(room_receive.getId(), when_receive);
			room_concerne.update_room(room_receive, when_receive);
		}
		this.raz();
	}
	
	public void raz(){
		for(Room any_room : rooms.values()){
			any_room.raz();
		}
	}
	
}
