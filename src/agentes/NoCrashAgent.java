package agentes;

import jade.core.Agent;

/**
 * @author marcelobns
 */
public class NoCrashAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("My name is " + getLocalName() + " SlimShade");
        addBehaviour(new NoCrashTicker(this, 1000));
    }

    @Override
    protected void takeDown() {
        System.out.println("Adeus mundo cruel!");
    }
}