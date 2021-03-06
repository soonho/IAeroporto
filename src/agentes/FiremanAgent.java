/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.util.StringTokenizer;

/**
 *
 * @author georg
 */
public class FiremanAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class FiremanBehaviour extends CyclicBehaviour {

        public FiremanBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.FAILURE:
                            if (msg.getContent().startsWith("EM_TERRA")
                                    || msg.getContent().startsWith("NO_POUSO")) {
                                //recebe o request e diz que está indo abastecer
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao = stok.nextToken();
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName()
                                        + " - SOS:" + status + " de " + aviao + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName()
                                );
                                //resposta para o goldfinger
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("RESGATANDO:" + aviao);
                                myAgent.send(reply);
                                //mensagem para o aviao
                                ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent("TE_RESGATANDO");
                                myAgent.send(acl);
                            } else {
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                            }
                            break;
                        default:
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
//                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
//                            myAgent.send(reply);
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
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("FiremanAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            FiremanBehaviour comportamento = new FiremanBehaviour(this);
            addBehaviour(comportamento);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
