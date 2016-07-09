package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class FilaBehaviour extends CyclicBehaviour {

    public FilaBehaviour(Agent a) {
        super(a);
    }

    public void informRadar(ACLMessage msg) {
        System.out.println("Recebendo Informação do RADAR: " + msg.getContent());
        if (true) {
            acionaGoldFinger();
        }
    }

    public void requestControlador(ACLMessage msg) {
        System.out.println("Recebendo Request do CONTROLADOR DE VOO: " + msg.getContent());
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o CONTROLADOR DE VOO");
    }

    public void requestNOCRASH(ACLMessage msg) {
        System.out.println("Recebendo Request do NOCRASH: " + msg.getContent());
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("NOCRASH", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o NOCRASH");
    }

    public void acionaGoldFinger() {
        System.out.println("Aciona GoldFinger");
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("GoldFinger", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o GoldFinger");
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getPerformative()) {
                case ACLMessage.INFORM:
                    if (msg.getContent().startsWith("RADAR")) {
                        informRadar(msg);
                    }
                    break;
                case ACLMessage.REQUEST:
                    if (msg.getContent().startsWith("CONTROLADOR")) {
                        requestControlador(msg);
                    }
                    if (msg.getContent().startsWith("NOCRASH")) {
                        requestNOCRASH(msg);
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
