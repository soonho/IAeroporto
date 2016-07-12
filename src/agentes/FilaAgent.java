package agentes;

import jade.core.Agent;

public class FilaAgent extends Agent {

    @Override
    protected void setup() {
        FilaBehaviour behaviour = new FilaBehaviour(this);
        addBehaviour(behaviour);
//        FilaBehaviourRefresh behaviourRefresh = new FilaBehaviourRefresh(this, 500);
//        addBehaviour(behaviourRefresh);
    }

}
