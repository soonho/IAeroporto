package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jdk.nashorn.internal.objects.Global;
import pojo.Aviao;

public class FilaBehaviour extends CyclicBehaviour {

    public FilaBehaviour(Agent a) {
        super(a);
    }

    public static String printDistancia() {
        double x, y, z, sqrt, menor = Global.Infinity;
        String nome = null;
        for (Aviao av : RadarAgent.radar) {
            System.out.println("Distancia: " + av.getLocalizacao());
            x = Math.pow(av.getxLocalizacao(), 2);
            y = Math.pow(av.getyLocalizacao(), 2);
            z = Math.pow(av.getzLocalizacao(), 2);
            sqrt = Math.sqrt(x + y + z);
            if (menor > sqrt) {
                menor = sqrt;
                nome = av.getNome();
            }
            System.out.println("O aviao mais proximo é = " + nome);
        }
        return nome;
    }

    public void requestControlador(ACLMessage msg) {
        System.out.println("Recebendo Request do CONTROLADOR DE VOO: " + msg.getContent());
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
        acl.setContent( "PERMIT_POUSO: " + printDistancia());
        myAgent.send(acl);
        System.out.println("Retornar Informação para o CONTROLADOR DE VOO");
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getPerformative()) {
                case ACLMessage.REQUEST:
                    if (msg.getContent().startsWith("CONTROLADOR")) {
                        requestControlador(msg);
                    }
                    break;

                case ACLMessage.FAILURE:
                    break;

                default:
                    break;
            }
        } else {
            block();
        }
    }

}
