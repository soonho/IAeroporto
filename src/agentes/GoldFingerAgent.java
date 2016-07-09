/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.util.ArrayList;
import java.util.StringTokenizer;
import pojo.Finger;

/**
 *
 * @author georg
 */
public class GoldFingerAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private static ArrayList<Finger> listaFingers = new ArrayList();

    private class AtualizaBehaviour extends TickerBehaviour {

        public AtualizaBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class GoldeFingerBehaviour extends CyclicBehaviour {

        public Finger getFreeFinger() {
            for (Finger temp : listaFingers) {
                if (temp.getFingerStatus() == false) {
                    return temp;
                } else {
                    return null;
                }

            }

            return null;
        }

        public int getTotalFreeFingers() {
            int total = 0;
            for (Finger temp : listaFingers) {
                if (temp.getFingerStatus() == false) {
                    total++;
                }
            }
            return total;
        }

        public int getTotalBusyFingers() {
            int total = 0;
            for (Finger temp : listaFingers) {
                if (temp.getFingerStatus() == true) {
                    total++;
                }
            }
            return total;
        }

        public GoldeFingerBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            /* TO DO */
 /*
                            if (msg.getContent().startsWith("ADD_RADAR")) {
                                //adiciona o aviao no radar
                                RadarAgent.addAviao((Finger) msg.getContentObject());
                                //registra no log
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - ADD_RADAR ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                //resposta para o aviao
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("ADDED_RADAR");
                                myAgent.send(reply);
                            } else if (msg.getContent().startsWith("POUSO")) {
                                //registra no log
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - POUSO ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID("Hermes", AID.ISLOCALNAME));
                                acl.setContent("POUSO" + msg.getContent());
                                myAgent.send(acl);
                            }
                             */
                            break;
                        case ACLMessage.INFORM: {
                            if (msg.getContent().startsWith("POUSO")) {
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String info = stok.nextToken();
                                String aviao = stok.nextToken();
                                //registra no log
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - POUSO COM SUCESSO! ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());

                                myLogger.log(Logger.INFO, "Total de fingers: " + listaFingers.size());
                                myLogger.log(Logger.INFO, "Finger Disponivel: " + this.getFreeFinger().getFingerNumber());

                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent("GO_TO_FINGER:" + this.getFreeFinger().getFingerNumber());
                                myAgent.send(acl);
//                                reply.setPerformative(ACLMessage.INFORM);
//                                reply.setContent("Vá para o Finger: " + this.getFreeFinger().getFingerNumber());
//                                myAgent.send(reply);
                            }
                            System.out.println("é meu parceiro...");
                        }
                        break;
                        case ACLMessage.FAILURE:

                            break;
                        default:
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            myAgent.send(reply);
                            break;
                    }
                } else {
                    block();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setup() {
        super.setup(); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Hello");

        Finger finger01 = new Finger(1);
        Finger finger02 = new Finger(2);
        Finger finger03 = new Finger(3);

        listaFingers.add(finger01);
        listaFingers.add(finger02);
        listaFingers.add(finger03);

        // Registration with the DF
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AgenteGoldFinger");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            GoldeFingerBehaviour comportamento = new GoldeFingerBehaviour(this);
            AtualizaBehaviour refresh = new AtualizaBehaviour(this, 500);

            addBehaviour(comportamento);

        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }

    }

}
