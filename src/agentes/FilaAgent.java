/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

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
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class FilaAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private static ArrayList<Aviao> filaPouso = new ArrayList();

    private class AtualizaBehaviour extends TickerBehaviour {

        public AtualizaBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class FilaBehaviour extends CyclicBehaviour {

        public FilaBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {

            ACLMessage msg = myAgent.receive();
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    if (msg.getPerformative() != ACLMessage.INFORM) {
                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            String content = msg.getContent();
                            if (content != null) {
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received Assignment Request from " + msg.getSender().getLocalName());
                                if (content.startsWith("acesso=")) {
//                                    Course course = CourseActions.loadByPK(Integer.parseInt(content.substring(content.indexOf("=") + 1)));
//                                    reply.setPerformative(ACLMessage.INFORM);
//                                    reply.setContent(UserActions.relatorioAcesso(
//                                            RoleContextActions.getCourseStudents(course.getId()), course.getId()));
                                } else if (content.startsWith("stats=")) {
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContent("ok");
                                }
                            } else {
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Unexpected request [" + content + "] received from " + msg.getSender().getLocalName());
                                reply.setPerformative(ACLMessage.REFUSE);
                                reply.setContent("( UnexpectedContent (" + content + "))");
                            }
                        } else {
                            myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Unexpected message [" + ACLMessage.getPerformative(msg.getPerformative()) + "] received from " + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            reply.setContent("( (Unexpected-act " + ACLMessage.getPerformative(msg.getPerformative()) + ") )");
                        }
                        send(reply);
                    }
                } else {
                    block();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } // END of inner class WaitPingAndReplyBehaviour

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("FilaAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            FilaBehaviour comportamento = new FilaBehaviour(this);
            AtualizaBehaviour refresh = new AtualizaBehaviour(this, 500);
            addBehaviour(comportamento);
            addBehaviour(refresh);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }

}
