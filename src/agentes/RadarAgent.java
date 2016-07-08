/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;
import java.util.ArrayList;
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class RadarAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    public static ArrayList<Aviao> radar = new ArrayList();

    private class RadarBehaviour extends TickerBehaviour {

        public RadarBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            for (Aviao av : radar) {
                System.out.println(av.getNome());
            }
        }
    }

    public static void setStatus(String aviao, String status) {
        for (Aviao av : radar) {
            if (av.getNome().equals(aviao)) {
                av.setSituacao(status);
                break;
            }
        }
    }

    public static void addAviao(Aviao aviao) {
        radar.add(aviao);
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
        sd.setType("RadarAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            RadarBehaviour comportamento = new RadarBehaviour(this, 1000);
            addBehaviour(comportamento);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
