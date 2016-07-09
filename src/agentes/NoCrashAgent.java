package agentes;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import pojo.*;
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
    ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
    Double aLocalX, aLocalY;
    Double aDestX, aDestY;
    
    Double bLocalX, bLocalY;
    Double bDestX, bDestY;
    
    Double coefA, coefB;
    
    NoCrashTicker(Agent agent, long delay){
        super(agent, delay);
        alert.setConversationId("NoCrashAlert");
    }
    @Override
    protected void onTick() {        
        for (Aviao aviaoA : RadarAgent.radar) {
            for (Aviao aviaoB : RadarAgent.radar) {
                if(crossRouteTest(aviaoA, aviaoB))
                    collisionAlert(aviaoA, aviaoB);
            }
        }        
    }
    protected boolean crossRouteTest(Aviao aviaoA, Aviao aviaoB){
        aLocalX = aviaoA.getxLocalizacao();
        aLocalY = aviaoA.getyLocalizacao();
        aDestX = aviaoA.getxDestino();
        aDestY = aviaoA.getyDestino();
            
        bLocalX = aviaoB.getxLocalizacao();
        bLocalY = aviaoB.getxLocalizacao();
        bDestX = aviaoB.getxDestino();
        bDestY = aviaoB.getyDestino();
                               
        coefA = (aLocalY-aDestY)/(aLocalX-aDestX);
        coefB = (bLocalY-bDestY)/(bLocalX-bDestX);        
        
        return (!coefA.equals(coefB) && !aDestX.equals(bDestX) && !aDestY.equals(bDestY));
    }
    protected void collisionAlert(Aviao aviaoA, Aviao aviaoB){   
        alert.addUserDefinedParameter("AviaoA", aviaoA.getNome());
        alert.addUserDefinedParameter("AviaoB", aviaoB.getNome());
                        
        alert.setContent("Alerta: " + aviaoA.getNome() +" e "+ aviaoB.getNome() + " estão em rota de colisão!!!");
        myAgent.send(alert);
    }
}