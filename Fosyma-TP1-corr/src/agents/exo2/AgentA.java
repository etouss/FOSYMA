package agents.exo2;
import java.util.List;

import behaviours.exo2.ReceiveMessageBehaviour;
import behaviours.exo2.SendNbValuesBehaviour;
import jade.core.Agent;


/**
 *  AgentA send its values to AgentSUM and receives the computed result using two behaviours
 * @author hc
 *
 */
public class AgentA extends Agent{

	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args.length!=0){
			System.out.println("Erreur lors du tranfert des parametres");
		}else{
			
		//Add the behaviours
		addBehaviour(new SendNbValuesBehaviour(this,10,"AgentSUM"));
		addBehaviour(new ReceiveMessageBehaviour(this));

		System.out.println("the sender agent "+this.getLocalName()+ " is started");
		
		}
	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}

}
