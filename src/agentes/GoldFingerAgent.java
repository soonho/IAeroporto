package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.util.ArrayList;
import java.util.StringTokenizer;
import pojo.Finger;

/**
 *
 * @author Mhayk
 */
public class GoldFingerAgent extends Agent {

    Logger myLogger = Logger.getMyLogger(getClass().getName());

    @Override
    protected void setup() {
        try {
            System.out.println("Hello my name is " + getLocalName());

            // Registration with the DF
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("AgenteGoldFinger");
            sd.setName(getName());
            sd.setOwnership("soonho");
            dfd.setName(getAID());
            dfd.addServices(sd);

            DFService.register(this, dfd);
            addBehaviour(new GoldeFingerBehaviour(this, 1000));

        } catch (Exception e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }
}

class GoldeFingerBehaviour extends TickerBehaviour {

    Logger myLogger = Logger.getMyLogger(getClass().getName());
    ArrayList<Finger> listaFingers = new ArrayList();
    ACLMessage received, reply;
    int total;

    public GoldeFingerBehaviour(Agent a, long delay) {
        super(a, delay);
        listaFingers.add(new Finger(1));
        listaFingers.add(new Finger(2));
        listaFingers.add(new Finger(3));
    }

    public Finger getFreeFinger() {
        for (Finger finger : listaFingers) {
            return finger.getStatus() == false ? finger : null;
        }
        return null;
    }

    public int getTotalFreeFingers() {
        total = 0;
        for (Finger finger : listaFingers) {
            if (finger.getStatus() == false) {
                total++;
            }
        }
        return total;
    }

    public int getTotalBusyFingers() {
        total = 0;
        for (Finger finger : listaFingers) {
            if (finger.getStatus() == true) {
                total++;
            }
        }
        return total;
    }

    @Override
    protected void onTick() {
        try {
            received = myAgent.receive();
            if (received != null) {
                reply = received.createReply();
                switch (received.getPerformative()) {
                    case ACLMessage.INFORM:
                        if (received.getContent().startsWith("POUSO")) {
                            StringTokenizer stok = new StringTokenizer(received.getContent(), ":", false);
                            String aviao = stok.nextToken();
                            myLogger.log(Logger.WARNING, "Agent " + myAgent.getLocalName() + " - POUSO COM SUCESSO! ["
                                    + ACLMessage.getPerformative(received.getPerformative())
                                    + "] recebida de " + received.getSender().getLocalName());

                            myLogger.log(Logger.INFO, "Total de fingers: " + listaFingers.size());
                            myLogger.log(Logger.INFO, "Finger Disponivel: " + this.getFreeFinger().getNumber());

                            reply.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                            reply.setContent("GO_TO_FINGER:" + this.getFreeFinger().getNumber());
                            myAgent.send(reply);
                        }
                        System.out.println("é meu parceiro...");
                        break;
                    default:
                        myLogger.log(Logger.WARNING, "Agent " + myAgent.getLocalName() + " - Mensagem inesperada ["
                                + ACLMessage.getPerformative(received.getPerformative()) + "] recebida de "
                                + received.getSender().getLocalName());
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        myAgent.send(reply);
                        break;
                }
            } else {
                block();
            }
        } catch (Exception e) {
            myLogger.log(Logger.SEVERE, "Agent " + myAgent.getLocalName() + " - comunication problem", e);
        }
    }
}
