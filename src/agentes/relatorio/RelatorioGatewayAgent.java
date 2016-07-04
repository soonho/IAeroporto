package agentes.relatorio;

/*****************************************************************

This agent receives the blackboard object  
and its content will be sent to the proper agent

 *****************************************************************/
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;
import servlet.relatorio.BlackBoardBean;

public class RelatorioGatewayAgent extends GatewayAgent {

    BlackBoardBean board = null;

    protected void processCommand(java.lang.Object obj) {

        if (obj instanceof BlackBoardBean) {

            board = (BlackBoardBean) obj;

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID(board.getReceiver(), AID.ISLOCALNAME));
            msg.setContent(board.getMessage());
            send(msg);
        }

    }

    public void setup() {
        // Waiting for the answer
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {

                ACLMessage msg = receive();

                if ((msg != null) && (board != null)) {
                    board.setMessage(msg.getContent());
                    releaseCommand(board);
                } else {
                    block();
                }
            }
        });

        super.setup();
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }
}
