package agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pojo.Aviao;

public class FilaBehaviour extends CyclicBehaviour {

    public FilaBehaviour(Agent a) {
        super(a);
    }

    public static String printDistancia() {
        double x, y, z, sqrt, menor = Double.MAX_VALUE;
        String nome = null;
        synchronized (RadarAgent.radar) {
            for (Aviao av : RadarAgent.radar) {
                if (av.getSituacao().equals("VOANDO")
                        || av.getSituacao().equals("ABASTECIDO")) {
//                    System.out.println("Distancia: " + av.getLocalizacao());
                    x = Math.pow(av.getxLocalizacao() - av.getxDestino(), 2);
                    y = Math.pow(av.getyLocalizacao() - av.getyDestino(), 2);
                    z = Math.pow(av.getzLocalizacao() - av.getzDestino(), 2);
                    sqrt = Math.sqrt(x + y + z);
                    if (menor > sqrt) {
                        menor = sqrt;
                        nome = av.getNome();
                        if (av.getSituacao().equals("VOANDO")) {
                            nome = "PERMIT_POUSO:" + nome;
                        } else if (av.getSituacao().equals("ABASTECIDO")) {
                            nome = "PERMIT_DECOLAGEM:" + nome;
                        }
                    }
                }
            }
        }
//        System.out.println("O aviao mais proximo é = " + nome);
        return nome;
    }

    public void requestControlador(ACLMessage msg) {
        System.out.println("Recebendo Request do CONTROLADOR DE VOO: " + msg.getContent());
        ACLMessage acl = msg.createReply();
        acl.setPerformative(ACLMessage.INFORM);
        acl.setContent(printDistancia());
        myAgent.send(acl);
//        System.out.println("Retornar Informação para o CONTROLADOR DE VOO");
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getPerformative()) {
                case ACLMessage.REQUEST:
                    if (msg.getContent().startsWith("NEXT_PLZ")) {
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
