package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point3D;
import pojo.Aviao;

/**
 *
 * @author marcelobarbosa
 */
public class NoCrashTicker extends TickerBehaviour {

    ACLMessage alert = new ACLMessage(ACLMessage.INFORM);

    Point3D pointA;
    Point3D pointB;

    Point3D destA;
    Point3D destB;

    Double coefA, coefB;
    Double distancia;
    String msgText;

    NoCrashTicker(Agent agent, long delay) {
        super(agent, delay);
        alert.setConversationId("NoCrashAlert");
        alert.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
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
        coefA = (pointA.getY() - destA.getY()) / (pointA.getX() - destA.getX());
        coefB = (pointB.getY() - destB.getY()) / (pointB.getX() - destB.getX());

        distancia = pointA.distance(pointB.getX(), pointB.getY(), pointB.getZ());

        if (!coefA.equals(coefB)) {
            return distancia <= 4000;
        }
        return false;
    }

    protected void collisionAlert(Aviao aviaoA, Aviao aviaoB) {
//        msgText = "RISCO de Colisão: " + aviaoA.getNome() + " e " + aviaoB.getNome();
        msgText = "COLISAO";
        alert.setContent(msgText);
        alert.addUserDefinedParameter("AviaoA", aviaoA.getNome());
        alert.addUserDefinedParameter("AviaoB", aviaoB.getNome());

        myAgent.send(alert);

        System.out.println(msgText);
    }

    protected void setLocal(Aviao aviaoA, Aviao aviaoB) {
        this.pointA = aviaoA.getLocalizacao();
        this.destA = aviaoA.getDestino();

        this.pointB = aviaoB.getLocalizacao();
        this.destB = aviaoB.getLocalizacao();
    }
}
