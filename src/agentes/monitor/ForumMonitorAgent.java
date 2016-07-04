package agentes.monitor;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import persistence.dao.DBMonitorAnswerActions;
import persistence.dao.DBMonitorLogActions;
import persistence.dao.ForumActions;
import persistence.dao.ForumPostActions;
import persistence.pojo.DBMonitorAnswer;
import persistence.pojo.DBMonitorLog;
import persistence.pojo.Forum;
import persistence.pojo.ForumPost;
import util.Constants;

public class ForumMonitorAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private static HashMap<String, String> questionType = new HashMap<String, String>();
    private static HashMap<String, String> questionAction = new HashMap<String, String>();
    private static HashMap<String, String> questionAnswer = new HashMap<String, String>();

    private class ForumMonitorBehaviour extends CyclicBehaviour {

        public ForumMonitorBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            for (Forum forum : ForumActions.newList()) {
                DBMonitorLog dblog = new DBMonitorLog();
                dblog.setExecutado(false);
                dblog.setIdItem(forum.getId());
                dblog.setTempoItem(forum.getDataModificado());
                dblog.setTabela(Constants.FORUM);
                dblog.setTipoOperacao(Constants.NEW_POST);
                DBMonitorLogActions.save(dblog);
            }
            for (ForumPost post : ForumPostActions.newList()) {
                StringTokenizer stok = new StringTokenizer(post.getMensagem(), "\r\n\t\f.;?!-", false);
                while (stok.hasMoreTokens()) {
                    String frase = stok.nextToken();
                    String qType = getQuestionType(frase);
                    String qAction = getQuestionAction(frase);
                    String qKey = getQuestionKey(frase);
                    
                    DBMonitorLog dblog = new DBMonitorLog();
                    dblog.setIdItem(post.getId());
                    dblog.setTempoItem(post.getHoraModificado());
                    dblog.setTabela(Constants.FORUM_POST);
                    if (!qType.isEmpty() && !qAction.isEmpty() && post.getMensagem().contains("?")) {
                        dblog.setExecutado(false);
                        dblog.setTipoOperacao(qType + ";" + qAction + ";" + qKey);
                    } else if (post.getMensagem().contains("?")) {
                        dblog.setExecutado(false);
                        dblog.setTipoOperacao(Constants.NEW_QUESTION);
                    } else {
                        dblog.setExecutado(true);
                        dblog.setTipoOperacao(Constants.NEW_QUESTION);
                    }
                    if (!DBMonitorLogActions.has(dblog)) {
                        DBMonitorLogActions.save(dblog);
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(ForumMonitorAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // END of inner class WaitPingAndReplyBehaviour

    @Override
    protected void setup() {
        this.initHashMaps();
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("MonitorAgent");
        sd.setName(getName());
        sd.setOwnership("soonho");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            ForumMonitorBehaviour mailBehaviour = new ForumMonitorBehaviour(this);
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
    }

    private String getQuestionType(String frase) {
        String retorno = "";
        String phrase = frase.toLowerCase();
        for (String str : questionType.keySet()) {
            if (phrase.contains(str) && !retorno.contains(str)) {
                retorno += questionType.get(str) + ",";
                break;//remover
            }
        }
        return retorno.substring(0, Math.max(retorno.length() - 1, 0));
    }

    private String getQuestionAction(String frase) {
        String retorno = "";
        String phrase = frase.toLowerCase();
        for (String str : questionAction.keySet()) {
            if (phrase.contains(str) && !retorno.contains(str)) {
                retorno += questionAction.get(str) + ",";
                break;//remover
            }
        }
        return retorno.substring(0, Math.max(retorno.length() - 1, 0));
    }

    private String getQuestionKey(String frase) {
        String retorno = "";
        String phrase = frase.toLowerCase();
        for (String str : questionAnswer.keySet()) {
            if (phrase.contains(str) && !retorno.contains(str)) {
                retorno += questionAnswer.get(str) + ",";
                break;//remover
            }
        }
        return retorno.substring(0, Math.max(retorno.length() - 1, 0));
    }

    private void initHashMaps() {
        for (DBMonitorAnswer dbma : DBMonitorAnswerActions.getListOf(Constants.ANSWER_QTYPE)) {
            questionType.put(dbma.getKey(), dbma.getValor());
        }
//        questionType.put("onde", "where");
//        questionType.put("donde", "where");
//        questionType.put("aonde", "where");
//        questionType.put("quando", "when");
//        questionType.put("como", "how");
//        questionType.put("quem", "who");
//        questionType.put("o que", "what");
//        questionType.put("o quê", "what");
//        questionType.put("que", "what");
//        questionType.put("quê", "what");
//        questionType.put("por que", "why");
//        questionType.put("por quê", "why");
//        questionType.put("porque", "why");
//        questionType.put("porquê", "why");
//        questionType.put("qual", "which");
//        questionType.put("quais", "which");

        for (DBMonitorAnswer dbma : DBMonitorAnswerActions.getListOf(Constants.ANSWER_QACTION)) {
            questionAction.put(dbma.getKey(), dbma.getValor());
        }
//        questionAction.put("é", "is");
//        questionAction.put("está", "state");
//        questionAction.put("situação", "state");
//        questionAction.put("foi", "was");
//        questionAction.put("era", "was");
//        questionAction.put("faz", "do");

        for (DBMonitorAnswer dbma : DBMonitorAnswerActions.getListOf(Constants.ANSWER)) {
            questionAnswer.put(dbma.getKey(), dbma.getKey());
        }
    }
}