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
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class GoldFingerAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

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
                            if (msg.getContent().startsWith("ADD_RADAR")) {
                                //adiciona o aviao no radar
                                RadarAgent.addAviao((Aviao) msg.getContentObject());
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
                            break;
                        case ACLMessage.INFORM: {
                            if (msg.getContent().startsWith("POUSO")) {
                                //registra no log
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - POUSO COM SUCESSO! ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID("Aviao", AID.ISLOCALNAME));
                                acl.setContent("POUSO" + msg.getContent());
                                myAgent.send(acl);
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
