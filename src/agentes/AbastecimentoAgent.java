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
public class AbastecimentoAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class AbastecimentoBehaviour extends CyclicBehaviour {

        public AbastecimentoBehaviour(Agent a) {
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
                            if (msg.getContent().startsWith("ABASTECER")) {
                                //recebe o request e diz que está indo abastecer
                                String aviao = msg.getUserDefinedParameter("AVIAO");
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName()
                                        + " - ABASTECENDO " + aviao + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName()
                                );
                                //resposta para o goldfinger
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("ABASTECENDO:" + aviao);
                                myAgent.send(reply);
                                //mensagem para o aviao
                                ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent("ABASTECENDO");
                                myAgent.send(acl);
                                //espera 10 segundos
                                Thread.sleep(10000);                                
                                //mensagem para o goldfinger
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("ABASTECIDO:" + aviao);
                                myAgent.send(reply);
                                //mensagem para o aviao
                                acl = new ACLMessage(ACLMessage.INFORM);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent("ABASTECIDO");
                                myAgent.send(acl);
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName()
                                        + " - ABASTECIDO " + aviao + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName()
                                );
                            } else {
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                            }
                            break;
                        case ACLMessage.INFORM:
                            if (msg.getContent().startsWith("ABASTECIDO")) {
                                //altera a situacao do aviao no radar
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao = stok.nextToken();
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - " + status + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + aviao);
                                //resposta para o aviao
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("OK");
                                myAgent.send(reply);
                                //mensagem para o goldfinger
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID("GoldFinger", AID.ISLOCALNAME));
                                acl.setContent(msg.getContent());
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
        sd.setType("AbastecimentoAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            AbastecimentoBehaviour comportamento = new AbastecimentoBehaviour(this);
            addBehaviour(comportamento);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
