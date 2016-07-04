package agentes.mailer;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import java.util.logging.Level;
import persistence.pojo.DBMonitorLog;

public class MailerAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private Email email = new Email();

    private class MailerBehaviour extends CyclicBehaviour {

        public MailerBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (msg.getPerformative() != ACLMessage.INFORM) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        Message content = null;
                        try {
                            content = formatMessage(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            java.util.logging.Logger.getLogger(MailerAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (content != null) {
//                        StringTokenizer strtok = new StringTokenizer(content.toString(), "₢");
                            myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received Mail Request from " + msg.getSender().getLocalName());
                            Email.enviar(content.getDestiny(), content.getTitle(), content.getContent());
//                        System.out.println(content.toString());
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("ok");
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
        }
    } // END of inner class WaitPingAndReplyBehaviour

    private Message formatMessage(Object obj) {
        Message msg = null;
        if (obj instanceof DBMonitorLog) {
            DBMonitorLog dbml = (DBMonitorLog) obj;
            msg = new Message("DBMonitorLog", dbml.toString(), "georgerr@hotmail.com");
        } else if (obj instanceof Message) {
            msg = (Message) obj;
        }
        return msg;
    }

    @Override
    protected void setup() {
        //setup da conta de email para enviar
        email.setSessao("soonho.bot@outlook.com", "iamahuman!");
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("MailerAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            MailerBehaviour PingBehaviour = new MailerBehaviour(this);
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