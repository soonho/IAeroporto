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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import persistence.dao.DBMonitorAnswerActions;
import persistence.dao.DBMonitorLogActions;
import persistence.dao.ForumActions;
import persistence.dao.ForumPostActions;
import persistence.dao.RoleContextActions;
import persistence.dao.UserActions;
import persistence.pojo.DBMonitorAnswer;
import persistence.pojo.DBMonitorLog;
import persistence.pojo.Forum;
import persistence.pojo.ForumPost;
import persistence.pojo.User;
import util.Constants;
import util.FrameWork;

public class ForumExecutorAgent extends Agent {

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
                                    myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received Forum Request from " + msg.getSender().getLocalName());
                                    Message message = null;
                                    ACLMessage acl = null;
                                    String title = "", text = "";
                                    Forum forum = null;

                                    if (content.getTipoOperacao().equals(Constants.NEW_POST)) {
                                        forum = ForumActions.loadByPK(content.getIdItem());
                                        title = "Novo fórum disponível!";
                                        text = "O fórum '" + forum.getNome() + "' está disponível!"
                                                + ", não deixe de participar!\n\n"
                                                + "Como você está cadastrado no curso '"
                                                + forum.getCurso().getNome() + "', essa mensagem lhe "
                                                + "foi enviada automaticamente. Caso queira que isso pare, "
                                                + "envie um email para georgerr@hotmail.com.";
                                        for (User user : RoleContextActions.getCourseStudents(forum.getCurso().getId())) {
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
                                    } else {
                                        forum = ForumActions.loadByPK(ForumPostActions.loadByPK(content.getIdItem()).getForumDiscussion().getForum().getId());
                                        ForumPost forumpost = ForumPostActions.loadByPK(content.getIdItem());
                                        title = "Atividade completada!";
                                        text = "O aluno " + UserActions.loadByPK(forumpost.getUser())
                                                + " postou uma nova dúvida no fórum '" 
                                                + forum.getNome() + "' em " 
                                                + FrameWork.formatUnixDate(forumpost.getHoraModificado())
                                                + "! Por favor, verifique e responda se necessário :)\n"
                                                + "Como você está cadastrado no curso '"
                                                + forum.getCurso().getNome() + "', essa mensagem lhe "
                                                + "foi enviada automaticamente. Caso queira que isso pare, "
                                                + "envie um email para georgerr@hotmail.com.";
                                        for (User user : RoleContextActions.getCourseTeachers(forum.getCurso().getId())) {
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
                                        
                                        if (!content.getTipoOperacao().isEmpty() && content.getTipoOperacao().contains(";")) {
                                            ForumPost newForumPost = new ForumPost();
                                            StringTokenizer st = new StringTokenizer(content.getTipoOperacao(), ";");
                                            String qType = st.nextToken();
                                            String qAction = st.nextToken();
                                            String qAnswer = st.nextToken();
                                            
                                            ArrayList<DBMonitorAnswer> aldbma = DBMonitorAnswerActions.getAnswer(qAnswer, qType, qAction);
                                            if (!aldbma.isEmpty()) {
                                                newForumPost.setParent(forumpost.getId());
                                                newForumPost.setUser(2);
                                                newForumPost.setHoraCriado(forumpost.getHoraCriado() + 1000);
                                                newForumPost.setHoraModificado(forumpost.getHoraCriado() + 1000);
                                                newForumPost.setForumDiscussion(forumpost.getForumDiscussion());
                                                newForumPost.setAssunto("RE: " + forumpost.getAssunto());
                                                newForumPost.setMensagem(aldbma.get(0).getValor());
                                                ForumPostActions.save(newForumPost);
                                            }
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

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ForumAgent");
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