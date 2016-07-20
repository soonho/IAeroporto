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

    public static ArrayList<Finger> listaFingers = new ArrayList();

    @Override
    protected void setup() {
        try {
//            System.out.println("Hello my name is " + getLocalName());

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
    ACLMessage received, reply;
    int total;

    public GoldeFingerBehaviour(Agent a, long delay) {
        super(a, delay);
        GoldFingerAgent.listaFingers.add(new Finger(1));
        GoldFingerAgent.listaFingers.add(new Finger(2));
        GoldFingerAgent.listaFingers.add(new Finger(3));
    }

    public Finger getFreeFinger() {
        for (Finger finger : GoldFingerAgent.listaFingers) {
            if (finger.getStatus() == false) {
                return finger;
            }
        }
        return null;
    }

    public int getTotalFreeFingers() {
        total = 0;
        for (Finger finger : GoldFingerAgent.listaFingers) {
            if (finger.getStatus() == false) {
                total++;
            }
        }
        return total;
    }

    public int getTotalBusyFingers() {
        total = 0;
        for (Finger finger : GoldFingerAgent.listaFingers) {
            if (finger.getStatus() == true) {
                total++;
            }
        }
        return total;
    }

    public void addFinger(int num, String aviao) {
        for (Finger finger : GoldFingerAgent.listaFingers) {
            if (finger.getNumber() == num) {
                finger.setStatus(Boolean.TRUE);
                finger.setAviao(aviao);
                break;
            }
        }
    }

    public void removeFinger(String aviao) {
        for (Finger finger : GoldFingerAgent.listaFingers) {
            if (finger.getAviao().equals(aviao)) {
                finger.setStatus(Boolean.FALSE);
                finger.setAviao("");
                break;
            }
        }
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
                            String status = stok.nextToken();
                            String aviao = stok.nextToken();
                            myLogger.log(Logger.WARNING, "Agent " + myAgent.getLocalName() + " - POUSO COM SUCESSO! ["
                                    + ACLMessage.getPerformative(received.getPerformative())
                                    + "] recebida de " + received.getSender().getLocalName());

                            myLogger.log(Logger.INFO, "Total de fingers: " + GoldFingerAgent.listaFingers.size());
                            myLogger.log(Logger.INFO, "Finger Disponivel: " + this.getFreeFinger().getNumber());

                            reply.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                            reply.setContent("GO_TO_FINGER:" + this.getFreeFinger().getNumber());
                            addFinger(this.getFreeFinger().getNumber(), aviao);
                            myAgent.send(reply);

                            reply = new ACLMessage(ACLMessage.REQUEST);
                            reply.addReceiver(new AID("Abastecimento", AID.ISLOCALNAME));
                            reply.setContent("ABASTECER");
                            reply.addUserDefinedParameter("AVIAO", aviao);
                            myAgent.send(reply);
                        } else if (received.getContent().startsWith("ABASTECIDO")) {
                            StringTokenizer stok = new StringTokenizer(received.getContent(), ":", false);
                            String info = stok.nextToken();
                            String aviao = stok.nextToken();
                            removeFinger(aviao);
                        }
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
