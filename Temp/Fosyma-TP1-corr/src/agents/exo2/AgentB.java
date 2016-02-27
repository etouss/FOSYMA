package agents.exo2;

import behaviours.exo2.SumNbReceivedValuesBehaviour;
import jade.core.Agent;


/**
 * 
 * @author hc
 *
 */
public class AgentB extends Agent{

	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args.length!=0){
			System.err.println("Erreur lors de la creation de l'agent somme");
			System.exit(-1);

		}else{

			//Add the behaviours
			addBehaviour(new SumNbReceivedValuesBehaviour(this, 10,"AgentA"));

			System.out.println("the receiver agent "+this.getLocalName()+ " is started");
		}
	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
}
