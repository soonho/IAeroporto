package agentes.executor;

import agentes.distribuidor.DistribuidorAgent;
import agentes.mailer.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import java.io.IOException;
import java.util.logging.Level;
import persistence.dao.AssignmentActions;
import persistence.dao.AssignmentSubmissionActions;
import persistence.dao.DBMonitorLogActions;
import persistence.dao.RoleContextActions;
import persistence.pojo.Assignment;
import persistence.pojo.AssignmentSubmission;
import persistence.pojo.DBMonitorLog;
import persistence.pojo.User;
import util.Constants;
import util.FrameWork;

public class AtividadeExecutorAgent extends Agent {

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
                            DBMonitorLog content = null;
                            try {
                                content = (DBMonitorLog) msg.getContentObject();
                            } catch (UnreadableException ex) {
                                java.util.logging.Logger.getLogger(MailerAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (content != null) {
                                if (DBMonitorLogActions.loadByPK(content.getId()).getExecutado()) {
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContent("already_ok");
                                } else {
                                    myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received Assignment Request from " + msg.getSender().getLocalName());
                                    Message message = null;
                                    ACLMessage acl = null;
                                    String title = "", text = "";
                                    Assignment assignment = AssignmentActions.loadByPK(content.getIdItem());

                                    if (content.getTipoOperacao().equals(Constants.ASSIGN_INICIO)) {
                                        title = "Nova atividade disponível!";
                                        text = "A atividade '" + assignment.getNome() + "' estará disponível "
                                                + "de " + FrameWork.formatUnixDate(assignment.getInicio())
                                                + " até " + FrameWork.formatUnixDate(assignment.getFim())
                                                + ", não deixe de respondê-la!\n\n"
                                                + "Como você está cadastrado no curso '"
                                                + assignment.getCourse().getNome() + "', essa mensagem lhe "
                                                + "foi enviada automaticamente. Caso queira que isso pare, "
                                                + "envie um email para georgerr@hotmail.com.";
                                        for (User user : RoleContextActions.getCourseStudents(assignment.getCourse().getId())) {
                                            acl = new ACLMessage(ACLMessage.REQUEST);
                                            acl.addReceiver(new AID("Hermes", AID.ISLOCALNAME));
                                            message = new Message(title, "Olá " + user.getNome() + "!\n" + text, user.getEmail());
                                            try {
                                                acl.setContentObject(message);
                                            } catch (IOException ex) {
                                                java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            myAgent.send(acl);
                                        }
                                    } else if (content.getTipoOperacao().equals(Constants.QUEST_COMPLETED)) {
                                        AssignmentSubmission as = AssignmentSubmissionActions.loadByPK(content.getIdItem());
                                        title = "Atividade completada!";
                                        text = "O aluno " + as.getUser().getNome() + " entregou a atividade '" 
                                                + assignment.getNome() + "' em " 
                                                + FrameWork.formatUnixDate(as.getHoraModificado())
                                                + ", não esqueça de avaliar a tarefa e dar nota ao aluno!\n"
                                                + "Como você está cadastrado no curso '"
                                                + assignment.getCourse().getNome() + "', essa mensagem lhe "
                                                + "foi enviada automaticamente. Caso queira que isso pare, "
                                                + "envie um email para georgerr@hotmail.com.";
                                        for (User user : RoleContextActions.getCourseTeachers(assignment.getCourse().getId())) {
                                            acl = new ACLMessage(ACLMessage.REQUEST);
                                            acl.addReceiver(new AID("Hermes", AID.ISLOCALNAME));
                                            message = new Message(title, "Olá " + user.getNome() + "!\n" + text, user.getEmail());
                                            try {
                                                acl.setContentObject(message);
                                            } catch (IOException ex) {
                                                java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            myAgent.send(acl);
                                        }
                                    }
                                    DBMonitorLogActions.executed(content);

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
            } else if (dbml.getTipoOperacao().equals("questcompleted")) {
                title = "Atividade completada!";
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