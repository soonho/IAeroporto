package agentes.relatorio;

import agentes.mailer.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import persistence.dao.CourseActions;
import persistence.dao.RoleContextActions;
import persistence.dao.UserActions;
import persistence.pojo.Assignment;
import persistence.pojo.Course;
import persistence.pojo.DBMonitorLog;
import persistence.pojo.User;
import util.FrameWork;

public class RelatorioAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class AssignmentBehaviour extends CyclicBehaviour {

        public AssignmentBehaviour(Agent a) {
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
                                    Course course = CourseActions.loadByPK(Integer.parseInt(content.substring(content.indexOf("=") + 1)));
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContent(UserActions.relatorioAcesso(
                                            RoleContextActions.getCourseStudents(course.getId()), course.getId()));
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

    private Message formatMessage(Object obj, User user, Assignment assign) {
        Message msg = null;
        if (obj instanceof DBMonitorLog) {
            String title = "", message = "";
            DBMonitorLog dbml = (DBMonitorLog) obj;
            if (dbml.getTipoOperacao().equals("timeavailable")) {
                title = "Nova atividade disponível!";
                message = "Olá " + user.getNome() + "!\n"
                        + "A atividade '" + assign.getNome() + "' estará disponível "
                        + "de " + FrameWork.formatUnixDate(assign.getInicio())
                        + " até " + FrameWork.formatUnixDate(assign.getFim())
                        + ", não esqueça de respondê-la!\n"
                        + "Como você está cadastrado no curso '"
                        + assign.getCourse().getNome() + "', essa mensagem lhe "
                        + "foi enviada automaticamente. Caso queira que isso pare, "
                        + "chame o soon_ho.";
            }
            msg = new Message(title, message, user.getEmail());
        }
        return msg;
    }

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ActivityAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            AssignmentBehaviour PingBehaviour = new AssignmentBehaviour(this);
            addBehaviour(PingBehaviour);
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