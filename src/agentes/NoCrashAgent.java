package agentes;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * @author marcelobns
 */
public class NoCrashAgent extends Agent {        
    @Override
    protected void setup() {
        System.out.println("My name is " + getLocalName() +" SlimShade");
        addBehaviour(new NoCrashTicker(this, 1000));
    }
    @Override
    protected void takeDown() {
        System.out.println("Adeus mundo cruel!");
    }    
}
class NoCrashTicker extends TickerBehaviour {
    NoCrashTicker(Agent agent, long delay){
        super(agent, delay);
    }
    @Override
    protected void onTick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}