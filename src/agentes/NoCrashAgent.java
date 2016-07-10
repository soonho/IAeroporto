package agentes;

import jade.core.AID;
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
        System.out.println("My name is " + getLocalName() + " SlimShade");
        addBehaviour(new NoCrashTicker(this, 1000));
    }

    @Override
    protected void takeDown() {
        System.out.println("Adeus mundo cruel!");
    }
}

class NoCrashTicker extends TickerBehaviour {

    ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
    Double aLocalX, aLocalY, aLocalZ;
    Double aDestX, aDestY;

    Double bLocalX, bLocalY, bLocalZ;
    Double bDestX, bDestY;

    Double coefA, coefB;

    NoCrashTicker(Agent agent, long delay) {
        super(agent, delay);
        alert.setConversationId("NoCrashAlert");
        alert.addReceiver(new AID("Joystick"));
    }

    @Override
    protected void onTick() {
        for (Aviao aviaoA : RadarAgent.radar) {
            for (Aviao aviaoB : RadarAgent.radar) {
                if (crossRouteTest(aviaoA, aviaoB)) {
                    collisionAlert(aviaoA, aviaoB);
                }
            }
        }
    }

    protected boolean crossRouteTest(Aviao aviaoA, Aviao aviaoB) {
        coefA = (aLocalY - aDestY) / (aLocalX - aDestX);
        coefB = (bLocalY - bDestY) / (bLocalX - bDestX);

        //TODO: verificar coefs, comparar distancia entre aeronaves e mesmo destino?
        return (!coefA.equals(coefB) && !aDestX.equals(bDestX) && !aDestY.equals(bDestY));
    }

    protected void collisionAlert(Aviao aviaoA, Aviao aviaoB) {
        alert.setContent("Alerta: " + aviaoA.getNome() + " e " + aviaoB.getNome() + " estão em rota de colisão!!!");
        alert.addUserDefinedParameter("AviaoA", aviaoA.getNome());
        alert.addUserDefinedParameter("AviaoB", aviaoB.getNome());

        myAgent.send(alert);

        System.out.println("Alerta: " + aviaoA.getNome() + " e " + aviaoB.getNome() + " estão em rota de colisão!!!");
    }

    protected void setLocal(Aviao aviaoA, Aviao aviaoB) {
        this.aLocalX = aviaoA.getxLocalizacao();
        this.aLocalY = aviaoA.getyLocalizacao();
        this.aDestX = aviaoA.getxDestino();
        this.aDestY = aviaoA.getyDestino();

        this.bLocalX = aviaoB.getxLocalizacao();
        this.bLocalY = aviaoB.getxLocalizacao();
        this.bDestX = aviaoB.getxDestino();
        this.bDestY = aviaoB.getyDestino();
    }
}
