package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class FilaBehaviourRefresh extends TickerBehaviour {

    public FilaBehaviourRefresh(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        System.out.println("Solicitando informações do RADAR");
        ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
        acl.addReceiver(new AID("Radar", AID.ISLOCALNAME));
        acl.setContent("FILA: Solicitando informações do Radar");
        myAgent.send(acl);
    }

}
