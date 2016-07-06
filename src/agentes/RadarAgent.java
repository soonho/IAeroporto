/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import java.util.ArrayList;
import pojo.Aviao;

/**
 *
 * @author georg
 */
public class RadarAgent extends Agent {

    public static ArrayList<Aviao> radar = new ArrayList();

    public static void setStatus(String aviao, String status) {
        for (Aviao av : radar) {
            if (av.getNome().equals(aviao)) {
                av.setSituacao(status);
            }
        }
    }

    public static void addAviao(Aviao aviao) {
        radar.add(aviao);
    }

    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setup() {
        super.setup(); //To change body of generated methods, choose Tools | Templates.
    }

}
