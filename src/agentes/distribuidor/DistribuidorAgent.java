package agentes.distribuidor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.io.IOException;
import java.util.logging.Level;
import persistence.dao.DBMonitorLogActions;
import persistence.pojo.DBMonitorLog;
import util.Constants;

public class DistribuidorAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class DistributeBehaviour extends CyclicBehaviour {

        public DistributeBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            for (DBMonitorLog monitor : DBMonitorLogActions.newList()) {
                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);

                if (monitor.getTabela().equals(Constants.ASSIGNMENT)
                        || monitor.getTabela().equals(Constants.ASSIGNMENT_SUBMISSION)) {
                    acl.addReceiver(new AID("PoliceActiv", AID.ISLOCALNAME));
                    try {
                        acl.setContentObject(monitor);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (monitor.getTabela().equals(Constants.FORUM)
                        || monitor.getTabela().equals(Constants.FORUM_POST)) {
                    acl.addReceiver(new AID("PoliceForum", AID.ISLOCALNAME));
                    try {
                        acl.setContentObject(monitor);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//                    acl.setContent("georgerr@hotmail.com₢" + dblog + "₢" + AssignmentActions.loadByPK(dblog.getIdItem()));
                myAgent.send(acl);
            }
//            System.out.println("Distribuidor distribuíndo.");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // END of inner class WaitPingAndReplyBehaviour

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("DistributeAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            DistributeBehaviour mailBehaviour = new DistributeBehaviour(this);
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
//        super.takeDown();
    }
    
//    public static void getAgents() {        
//        SearchConstraints constraints = new SearchConstraints();
//        constraints.setMaxResults((long) -1);
//        AMSAgentDescription active = new AMSAgentDescription();
//        active.setState(AMSAgentDescription.ACTIVE);
//        try {
//            for (AMSAgentDescription d : AMSService.search(this.myAgent, active, constraints)) {
//                System.out.println(d.getName().getName() + "," + d.getState());
//            }
//        } catch (FIPAException ex) {
//            java.util.logging.Logger.getLogger(DistribuidorAgent.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}