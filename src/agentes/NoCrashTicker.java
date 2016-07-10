package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import pojo.Aviao;

/**
 *
 * @author marcelobarbosa
 */
public class NoCrashTicker extends TickerBehaviour {

    ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
    Double aLocalX, aLocalY, aLocalZ;
    Double aDestX, aDestY;

    Double bLocalX, bLocalY, bLocalZ;
    Double bDestX, bDestY;

    Double coefA, coefB;
    Double distancia;

    NoCrashTicker(Agent agent, long delay) {
        super(agent, delay);
        alert.setConversationId("NoCrashAlert");
        alert.addReceiver(new AID("Joystick"));
    }

    @Override
    protected void onTick() {
        for (Aviao aviaoA : RadarAgent.radar) {
            for (Aviao aviaoB : RadarAgent.radar) {
                if (!aviaoA.equals(aviaoB) && crossRouteTest(aviaoA, aviaoB)) {
                    collisionAlert(aviaoA, aviaoB);
                }
            }
        }
    }

    protected boolean crossRouteTest(Aviao aviaoA, Aviao aviaoB) {
        setLocal(aviaoA, aviaoB);
        coefA = (aLocalY - aDestY) / (aLocalX - aDestX);
        coefB = (bLocalY - bDestY) / (bLocalX - bDestX);

        distancia = Math.sqrt(Math.pow((aLocalX - bLocalX), 2) + Math.pow((aLocalY - bLocalY), 2));
        if (distancia <= 1000) {
            return !coefA.equals(coefB) && !aDestX.equals(bDestX) && !aDestY.equals(bDestY);
        }
        return false;
    }

    protected void collisionAlert(Aviao aviaoA, Aviao aviaoB) {
        String msgText = "Alerta: " + aviaoA.getNome() + " e " + aviaoB.getNome() + " estão em rota de colisão!!!";
        alert.setContent(msgText);
        alert.addUserDefinedParameter("AviaoA", aviaoA.getNome());
        alert.addUserDefinedParameter("AviaoB", aviaoB.getNome());

        myAgent.send(alert);

        System.out.println(msgText);
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
