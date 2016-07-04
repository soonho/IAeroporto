package agentes.monitor;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;
import java.util.logging.Level;
import persistence.dao.AssignmentActions;
import persistence.dao.AssignmentSubmissionActions;
import persistence.dao.DBMonitorLogActions;
import persistence.pojo.Assignment;
import persistence.pojo.AssignmentSubmission;
import persistence.pojo.DBMonitorLog;
import util.Constants;

public class AtividadeMonitorAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class AssignmentMonitorBehaviour extends CyclicBehaviour {

        public AssignmentMonitorBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            for (Assignment assign : AssignmentActions.newList()) {
                DBMonitorLog dblog = new DBMonitorLog();
                dblog.setExecutado(false);
                dblog.setIdItem(assign.getId());
                dblog.setTempoItem(assign.getInicio());
                dblog.setTabela(Constants.ASSIGNMENT);
                dblog.setTipoOperacao(Constants.ASSIGN_INICIO);
                DBMonitorLogActions.save(dblog);
            }
            for (AssignmentSubmission assign : AssignmentSubmissionActions.newList()) {
                DBMonitorLog dblog = new DBMonitorLog();
                dblog.setExecutado(false);
                dblog.setIdItem(assign.getId());
                dblog.setTempoItem(assign.getHoraModificado());
                dblog.setTabela(Constants.ASSIGNMENT_SUBMISSION);
                dblog.setTipoOperacao(Constants.QUEST_COMPLETED);
                DBMonitorLogActions.save(dblog);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(AtividadeMonitorAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // END of inner class WaitPingAndReplyBehaviour

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("MonitorAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            AssignmentMonitorBehaviour mailBehaviour = new AssignmentMonitorBehaviour(this);
            addBehaviour(mailBehaviour);
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