/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javafx.geometry.Point3D;
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class AviaoAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private Aviao aviao;
    private Boolean isRegistered = false;
    private static final String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String[] numeros = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private class RefreshBehaviour extends TickerBehaviour {

        public RefreshBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            
        }

    }

    private class AviaoBehaviour extends CyclicBehaviour {

        public AviaoBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            if (!isRegistered) {
                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
                acl.setContent("ADD_RADAR:"+getName()+":"+aviao.stringfy());
                try {
                    acl.setContentObject(aviao);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(AviaoAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                myAgent.send(acl);
            }

            ACLMessage msg = myAgent.receive();
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.FAILURE:
                            if (msg.getContent().startsWith("EM_TERRA")
                                    || msg.getContent().startsWith("NO_POUSO")) {
                                //recebe o request e diz que está indo abastecer
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String status = stok.nextToken();
                                String aviao = stok.nextToken();
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName()
                                        + " - SOS:" + status + " de " + aviao + " ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName()
                                );
                                //resposta para o goldfinger
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("RESGATANDO:" + aviao);
                                myAgent.send(reply);
                                //mensagem para o aviao
                                ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                                acl.addReceiver(new AID(aviao, AID.ISLOCALNAME));
                                acl.setContent("TE_RESGATANDO");
                                myAgent.send(acl);
                            } else {
                                myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                        + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                        + msg.getSender().getLocalName());
                            }
                            break;
                        default:
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
//                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
//                            myAgent.send(reply);
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
        //Gerar novo aviao
        aviao = new Aviao(
                getRandomName(),
                getRandomPoint(),
                new Point3D(0, 0, 0),
                (int) (Math.random() * 800) + 3000,
                "VOANDO",
                (Math.random() * 300) + 700);
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AviaoAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            RefreshBehaviour comportamento = new RefreshBehaviour(this, 1000);
            AviaoBehaviour comportamento2 = new AviaoBehaviour(this);
            addBehaviour(comportamento);
            addBehaviour(comportamento2);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

    private static Point3D getRandomPoint() {
        return new Point3D(Math.random() * 10000, Math.random() * 10000, 30000);
    }

    private static String getRandomItem(String[] array) {
        return array[(int) (Math.random() * (array.length - 1))];
    }

    private static String getRandomName() {
        return getRandomItem(letras) + getRandomItem(letras)
                + getRandomItem(numeros) + getRandomItem(numeros) + getRandomItem(numeros);
    }

}
