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
import javafx.geometry.Point3D;
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class ControladorAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class ControladorBehaviour extends CyclicBehaviour {

        public ControladorBehaviour(Agent a) {
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
                            if (msg.getContent().startsWith("ADD_RADAR")) {
                                //adiciona o aviao no radar
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String order = stok.nextToken();
                                Aviao aviao = new Aviao(
                                        stok.nextToken(),
                                        new Point3D(Double.parseDouble(stok.nextToken()),
                                                Double.parseDouble(stok.nextToken()),
                                                Double.parseDouble(stok.nextToken())),
                                        new Point3D(Double.parseDouble(stok.nextToken()),
                                                Double.parseDouble(stok.nextToken()),
                                                Double.parseDouble(stok.nextToken())),
                                        Integer.parseInt(stok.nextToken()),
                                        stok.nextToken(),
                                        Double.parseDouble(stok.nextToken()));
                                RadarAgent.addAviao(aviao);
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - ADD_RADAR ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                //resposta para o aviao
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("ADDED_RADAR");
                                myAgent.send(reply);
                            } else if (msg.getContent().startsWith("POUSO")) {
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - POUSO ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                //enviar mensagem para FilaAgent
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID("Fila", AID.ISLOCALNAME));
                                acl.setContent("POUSO" + msg.getContent());
                                myAgent.send(acl);
                            } else {
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                            }
                            break;
                        case ACLMessage.INFORM:
                            if (msg.getContent().startsWith("POUSANDO")
                                    || msg.getContent().startsWith("POUSEI")) {
                                //altera a situacao do aviao no radar
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao = stok.nextToken();
                                RadarAgent.setStatus(aviao, status);
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - " + status + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + aviao);
                                //resposta para o aviao
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("OK");
                                myAgent.send(reply);
                                if (status.equals("POUSEI")) {
                                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                    acl.addReceiver(new AID("GoldFinger", AID.ISLOCALNAME));
                                    acl.setContent("POUSO:" + aviao);
                                    myAgent.send(acl);
                                }
                            } else if (msg.getContent().startsWith("COLISAO")) {
                                //registra no log
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - COLISÃO ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao1 = stok.nextToken();
                                String aviao2 = stok.nextToken();
                                //info dos avioes
                                Aviao um = RadarAgent.getAviao(aviao1);
                                Aviao dois = RadarAgent.getAviao(aviao2);
                                //request para o mais baixo descer mais
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                if (um.getzLocalizacao() > dois.getzLocalizacao()) {
                                    acl.addReceiver(new AID(dois.getNome(), AID.ISLOCALNAME));
                                } else {
                                    acl.addReceiver(new AID(um.getNome(), AID.ISLOCALNAME));
                                }
                                acl.setContent("DESCER:2000");
                                myAgent.send(acl);
                            } else if (msg.getContent().startsWith("PERMIT_POUSO")
                                    || msg.getContent().startsWith("PERMIT_DECOLAR")) {
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao = stok.nextToken();
                                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent(status);
                                myAgent.send(acl);
                            } else {
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                            }
                            break;
                        case ACLMessage.FAILURE:
                            //registra no log
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - EMERGÊNCIA ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
                            //enviar mensagem para os bombeiros
//                            ACLMessage acl = new ACLMessage(ACLMessage.FAILURE);
//                            acl.addReceiver(new AID("Fireman", AID.ISLOCALNAME));
//                            acl.setContent("NO_POUSO:" + msg.getContent());
//                            myAgent.send(acl);
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
        sd.setType("ControladorAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            ControladorBehaviour comportamento = new ControladorBehaviour(this);
            addBehaviour(comportamento);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
