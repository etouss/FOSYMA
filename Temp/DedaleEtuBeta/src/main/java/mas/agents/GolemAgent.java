//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mas.agents;

import env.Couple;
import env.Environment;
import jade.core.behaviours.TickerBehaviour;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import mas.abstractAgent;

public class GolemAgent extends abstractAgent {
    private static final long serialVersionUID = 2703609263614775545L;

    public GolemAgent() {
    }

    protected void setup() {
        super.setup();
        Object[] args = this.getArguments();
        if(args[0] != null) {
            this.deployWumpus((Environment)args[0]);
        } else {
            System.err.println("Malfunction during parameter\'s loading of agent" + this.getClass().getName());
            System.exit(-1);
        }

        this.addBehaviour(new GolemAgent.GolemRandomWalkBehaviour(this));
        System.out.println("the  agent " + this.getLocalName() + " is started");
    }

    protected void takeDown() {
    }

    public class GolemRandomWalkBehaviour extends TickerBehaviour {
        private static final long serialVersionUID = 9088209402507795289L;
        private boolean finished = false;
        private Environment realEnv;

        public GolemRandomWalkBehaviour(abstractAgent myagent) {
            super(myagent,500);
        }

        public void onTick() {
            String myPosition = GolemAgent.this.getCurrentPosition();
            if(myPosition != "") {
                List lobs = GolemAgent.this.observe();
                System.out.println("lobs: " + lobs);
/*
                try {
                    System.out.println("Press a key to allow the wumpus " + this.myAgent.getLocalName() + " to move to the next step ");
                    System.in.read();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }*/

                Random r = new Random();
                int moveId = r.nextInt(lobs.size());
                GolemAgent.this.moveTo((String)((Couple)lobs.get(moveId)).getLeft());
            } else {
                System.err.println("Empty posit");
                System.exit(40);
            }

        }

    }
}
