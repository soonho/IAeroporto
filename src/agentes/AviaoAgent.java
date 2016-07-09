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
            double andou = aviao.getVelocidade() / 3.6;
            double m = (aviao.getyLocalizacao() - aviao.getyDestino())
                    / (aviao.getxLocalizacao() - aviao.getyDestino());
            double alfa = Math.atan(m);
            aviao.setxLocalizacao(aviao.getxLocalizacao()
                    + (aviao.getxLocalizacao() > 0 ? -1 : 1) * Math.abs(Math.cos(alfa) * andou));
            aviao.setyLocalizacao(aviao.getyLocalizacao()
                    + (aviao.getyLocalizacao() > 0 ? -1 : 1) * Math.abs(Math.sin(alfa) * andou));
            System.out.println(aviao.getxLocalizacao() + "," + aviao.getyLocalizacao());
            for (Aviao av : RadarAgent.radar) {
                if (av.getNome().equals(aviao.getNome())) {
                    av.setxLocalizacao(aviao.getxLocalizacao());
                    av.setyLocalizacao(aviao.getyLocalizacao());
                    break;
                }
            }
        }

    }

    private class AviaoBehaviour extends CyclicBehaviour {

        public AviaoBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = null;
            if (!isRegistered) {
                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
                acl.setContent("ADD_RADAR:" + aviao.getNome() + ":" + aviao.stringfy());
                myAgent.send(acl);
                msg = myAgent.blockingReceive();
            } else {
                msg = myAgent.receive();
            }
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            if (msg.getContent().startsWith("GO_TO_FINGER")) {
                                StringTokenizer stok = new StringTokenizer(msg.getContent(), ":", false);
                                String info = stok.nextToken();
                                String finger = stok.nextToken();
                                aviao.setSituacao("FINGER:" + finger);
                                myLogger.log(Logger.INFO, "Indo para o Finger: " + finger);
                            }
                            break;
                        case ACLMessage.INFORM:
                            if (msg.getContent().startsWith("ADDED_RADAR")) {
                                isRegistered = true;
                            }
                            break;
                        case ACLMessage.FAILURE:
                            //mensagem para o Controlador e para o Fireman
                            ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                            acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
                            acl.addReceiver(new AID("Fireman", AID.ISLOCALNAME));
                            if (aviao.getSituacao().equals("VOANDO")
                                    || aviao.getSituacao().equals("POUSANDO")) {
                                acl.setContent("NO_POUSO");
                            } else {
                                acl.setContent("EM_TERRA");
                            }
                            myAgent.send(acl);
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - FALHA ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
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
                //                getRandomName(),
                getLocalName(),
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
        return new Point3D((Math.random() < 0.5 ? -1 : 1) * Math.random() * 10000, (Math.random() < 0.5 ? -1 : 1) * Math.random() * 10000, 30000);
    }

    private static String getRandomItem(String[] array) {
        return array[(int) (Math.random() * (array.length - 1))];
    }

    private static String getRandomName() {
        return getRandomItem(letras) + getRandomItem(letras)
                + getRandomItem(numeros) + getRandomItem(numeros) + getRandomItem(numeros);
    }

}
