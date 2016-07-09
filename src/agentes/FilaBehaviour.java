package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import javafx.geometry.Point3D;
import jdk.nashorn.internal.objects.Global;
import pojo.Aviao;

public class FilaBehaviour extends CyclicBehaviour {

    public static ArrayList<Aviao> aviaoes = new ArrayList();

    public static void addAviao(Aviao aviao) {
        for (Aviao av : aviaoes) {
            if (av.getNome().equals(aviao)) {
                return;
            }
        }
        aviaoes.add(aviao);
    }

    public static void printDistancia() {
        double x, y, z, sqrt, menor = Global.Infinity;
        for (Aviao av : aviaoes) {
            System.out.println("Distancia: " + av.getLocalizacao());
            x = Math.pow(av.getxLocalizacao(), 2);
            y = Math.pow(av.getyLocalizacao(), 2);
            z = Math.pow(av.getzLocalizacao(), 2);
            sqrt = Math.sqrt(x + y + z);
            if (menor > sqrt) {
                menor = sqrt;
            }
            System.out.println("SQRT = " + sqrt);
            System.out.println("MENOR = " + menor);
        }
    }

    public static void printName() {
        for (Aviao av : aviaoes) {
            System.out.println("Nome: " + av.getNome());
        }
    }

    public FilaBehaviour(Agent a) {
        super(a);
    }

    public void informRadar(ACLMessage msg) {
        System.out.println("Recebendo Informação do RADAR: " + msg.getContent());
        Aviao aviao = new Aviao(
                "Aviaozinhow",
                new Point3D(4, -2, 3),
                new Point3D(0, 0, 0),
                Integer.parseInt("3"),
                "Rapidinho",
                Double.parseDouble("2"));
        FilaBehaviour.addAviao(aviao);

        Aviao aviao3 = new Aviao(
                "Aviaozinhow",
                new Point3D(14, 12, 13),
                new Point3D(0, 0, 0),
                Integer.parseInt("3"),
                "Rapidinho",
                Double.parseDouble("2"));
        FilaBehaviour.addAviao(aviao3);
//        System.out.println(new Point3D(4, -2, 3));

        Aviao aviao2 = new Aviao(
                "Aviaozinhow2",
                new Point3D(3, 3, -3),
                new Point3D(0, 0, 0),
                Integer.parseInt("1"),
                "lento",
                Double.parseDouble("1"));
        FilaBehaviour.addAviao(aviao2);

//        System.out.println(new Point3D(3, 3, -3));
        printDistancia();
        printName();
        System.out.println("OK");
//        if (true) {
//            acionaGoldFinger();
//        }
    }

    public void requestControlador(ACLMessage msg) {
        System.out.println("Recebendo Request do CONTROLADOR DE VOO: " + msg.getContent());
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("Joystick", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o CONTROLADOR DE VOO");
    }

    public void requestNOCRASH(ACLMessage msg) {
        System.out.println("Recebendo Request do NOCRASH: " + msg.getContent());
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("NOCRASH", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o NOCRASH");
    }

    public void acionaGoldFinger() {
        System.out.println("Aciona GoldFinger");
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(new AID("GoldFinger", AID.ISLOCALNAME));
        acl.setContent("FILA: enviando informacoes de fila");
        myAgent.send(acl);
        System.out.println("Retornar Informação para o GoldFinger");
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getPerformative()) {
                case ACLMessage.INFORM:
                    if (msg.getContent().startsWith("RADAR")) {
                        informRadar(msg);
                    }
                    break;
                case ACLMessage.REQUEST:
                    if (msg.getContent().startsWith("CONTROLADOR")) {
                        requestControlador(msg);
                    }
                    if (msg.getContent().startsWith("NOCRASH")) {
                        requestNOCRASH(msg);
                    }
                    break;

                case ACLMessage.FAILURE:
                    break;

                default:
                    break;
            }
        } else {
            block();
        }
    }

}
