package mas.behaviours;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import env.Attribute;

import env.Couple;

import jade.core.behaviours.TickerBehaviour;
import mas.agents.DummyExploAgent;
import mas.structuregraph.MyGraph;
import mas.structuregraph.Node_not_known_Exeption;
import mas.structuregraph.Noeud;

/**************************************
 * 
 * 
 * 				BEHAVIOUR
 * 
 * 
 **************************************/


public class RandomWalkBehaviour extends TickerBehaviour{
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private MyGraph graph;

	public RandomWalkBehaviour (final mas.abstractAgent myagent,MyGraph graph) {
		super(myagent, 500);
		this.graph = graph;
		//super(myagent);
	}

	@Override
	public void onTick() {
		((DummyExploAgent)this.myAgent).ticking();
		//Example to retrieve the current position
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		try {
			graph.return_node(myPosition).there();
		} catch (Node_not_known_Exeption e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}

		if (myPosition!=""){
			//List of observable from the agent's current position
			List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();//myPosition
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			/*Add to graph*/
			Noeud self = null;
			try {
				self = graph.return_node(myPosition);
			} catch (Node_not_known_Exeption e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				System.out.println("NO WAY");
			}
			for(Couple<String,List<Attribute>> couple : lobs ){
				if(couple.getLeft().equals(myPosition))continue;
				Noeud node = null;
				try {
					node = graph.return_node(couple.getLeft());
				} catch (Node_not_known_Exeption e) {
					node = new Noeud(couple.getLeft(),graph,((DummyExploAgent)this.myAgent).ticked());
					graph.add_node(node);
				}
				graph.add_edge(self, node,((DummyExploAgent)this.myAgent).ticked());
			}

			//Little pause to allow you to follow what is going on
			/*
			try {
				System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/


			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();

			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Attribute a:lattribute){
				switch (a) {
				case TREASURE:
					System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("Value of the treasure on the current position: "+a.getValue());
					System.out.println("The agent grabbed :"+((mas.abstractAgent)this.myAgent).pick());
					System.out.println("the remaining backpack capacity is: "+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("The value of treasure on the current position: (unchanged before a new call to observe()): "+a.getValue());
					b=true;
					break;

				default:
					break;
				}
			}

			//If the agent picked (part of) the treasure
			if (b){
				List<Couple<String,List<Attribute>>> lobs2=((mas.abstractAgent)this.myAgent).observe();//myPosition
				System.out.println("lobs after picking "+lobs2);
			}

			int i = 0;
			boolean ok = false;
			for(i = 0;i<lobs.size();i++){
				if(!graph.already_known(lobs.get(i).getLeft())){
					ok = true;
					break;
				}
			}
			//Random move from the current position
			if(!ok){
				Random r= new Random();
				i=r.nextInt(lobs.size());
			}

			//The move action (if any) should be the last action of your behaviour
			
			((mas.abstractAgent)this.myAgent).moveTo(lobs.get(i).getLeft());
		}

	}

}