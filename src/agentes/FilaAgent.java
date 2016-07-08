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
        }

    }

    private class FilaBehaviour extends CyclicBehaviour {

        public FilaBehaviour(Agent a) {
        }

        @Override
        public void action() {
        }
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void takeDown() {
    }

}
