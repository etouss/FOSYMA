package agents.exo1;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * This agent is very simple. It only possesses a behaviour that allows him to send a single message to another agent
 * 
 * In a real application,for genericity and clarity, an agent class and the behaviour classes should not be in the same file.  
 * 
 * @author Cédric Herpson
 *
 */
public class AgentA extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1968856210218561967L;
	protected List<String> data;
	
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that AgentA is launched for the first time. 
	 * 			1 set the agent attributes 
	 *	 		2 add the behaviour(s)
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			data = (List<String>) args[0];

		}else{
			System.out.println("Erreur lors du tranfert des parametres");
		}

		//Add the behaviours
		addBehaviour(new SendMessage(this));

		System.out.println("the sender agent "+this.getLocalName()+ " is started");
		
	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}

	
	/**************************************
	 * 
	 * 
	 * 				BEHAVIOURS
	 * 
	 * 
	 **************************************/

	public class SendMessage extends SimpleBehaviour{
		/**
		 * When an agent choose to communicate with others agents in order to reach a precise decision, 
		 * it tries to form a coalition. This behaviour is the first step of the paxos algorithm
		 *  
		 */
		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;

		public SendMessage(final Agent myagent) {
			super(myagent);

		}


		public void action() {
			//Create a message in order to send it to the choosen agent
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			//msg.setLanguage(MyOntology.LANGUAGE);
			//msg.setOntology(MyOntology.ONTOLOGY_NAME);
			//msg.setProtocol(MyOntology.PAXOS_PREPARE);
			
			msg.addReceiver(new AID("Agent1", AID.ISLOCALNAME)); // hardcoded= bad. In a real application, the agent must receive this knowledge during the initialisation, or through communication with other agents/the DF. 
				
			msg.setContent(((AgentA)this.myAgent).data.get(0));

			this.myAgent.send(msg);
			this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.
			
			System.out.println("----> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());

		}

		public boolean done() {
			return finished;
		}

	}

}
