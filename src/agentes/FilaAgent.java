package agentes;

import jade.core.Agent;

public class FilaAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Testando");

        FilaBehaviour behaviour = new FilaBehaviour(this);
        addBehaviour(behaviour);
    }

}
