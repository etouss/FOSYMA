package behaviours.exo2;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.scene.paint.RadialGradient;

/***
 * This behaviour allows the agent who possess it to send nb random int within [0-100[ to another agent whose local name is given in parameters
 * 
 * 
 * @author Cédric Herpson
 *
 */

public class SendNbValuesBehaviour extends SimpleBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	private int nbValues=0;
	private int nbMessagesSent=0;
	private String receiverName="";
	Random r;

	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param nbValues the number of messages that should be sent to the receiver
	 * @param receiverName The local name of the receiver agent
	 */
	public SendNbValuesBehaviour(final Agent myagent,int nbValues, String receiverName) {
		super(myagent);
		this.nbValues=nbValues;
		this.receiverName=receiverName;
		this.r= new Random();
		

	}


	public void action() {
		
		
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(this.receiverName, AID.ISLOCALNAME));  
			
		//2° compute the random value		
		msg.setContent(((Integer)r.nextInt(100)).toString());
		this.myAgent.send(msg);
		nbMessagesSent++;
		
		if(nbMessagesSent>=nbValues){
			this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.
		}
		
		System.out.println(this.myAgent.getLocalName()+" ----> Message number "+this.nbMessagesSent+" sent to "+this.receiverName+" ,content= "+msg.getContent());

	}

	public boolean done() {
		return finished;
	}

}
