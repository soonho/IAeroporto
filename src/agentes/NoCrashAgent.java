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
    private String empresa;
    private String voo;
    private String situacao;
    private int location;
    private int destiny;
    
    @Override
    protected void setup() {
        System.out.println("My name is " + getLocalName() +" SlimShade");
        
        addBehaviour(new Transponder(this, 2000));
        addBehaviour(new CollisionAvoidance(this, 1000));
        
        Object[] args = getArguments();
        if(args.length == 3){
            this.empresa = (String)args[0];
            this.voo = (String)args[1];
            this.situacao = (String)args[2];
        }                        
    }
    @Override
    protected void takeDown() {
        System.out.println("Adeus mundo cruel!");
    }    
}
/**
 * Behavior: Sender of location to all agents
 */
class Transponder extends TickerBehaviour {        
    private AMSAgentDescription [] agents = null;
    private SearchConstraints searchConstraints = new SearchConstraints();
    
    Transponder(Agent agent, long delay){
        super(agent, delay);
    }
    @Override
    protected void onTick() {
        try{                   
            searchConstraints.setMaxResults(new Long(-1));            
            agents = AMSService.search(myAgent, new AMSAgentDescription(), searchConstraints);
                                               
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            
            for (int i = 0; i < agents.length; i++) {                
                msg.addReceiver(agents[i].getName());
            }
            
            msg.setContent("location of " + myAgent.getLocalName());
            myAgent.send(msg);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }        
    }
}
/**
 * Behavior: Receiver of locations and measures
 */
class CollisionAvoidance extends TickerBehaviour {    
    CollisionAvoidance(Agent agent, long delay){
        super(agent, delay);
    }
    @Override
    public void onTick() {
        MessageTemplate template = MessageTemplate.not(MessageTemplate.MatchSender(myAgent.getAID()));
        ACLMessage msg = myAgent.receive(template);
        if(msg != null){
            System.out.println(myAgent.getLocalName() + " Receiving : " + msg.getContent());
        }        
    }    
}

/**
 * Behavior: Moves on map
 */
class Piloto extends TickerBehaviour {
    Piloto(Agent agent, long delay){
        super(agent, delay);
    }
    @Override
    protected void onTick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

