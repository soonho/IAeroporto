/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javafx.geometry.Point3D;

/**
 *
 * @author soonho
 */
public class Util {

    public static Double getDistancia2D(Point3D localizacao, Point3D destino) {
        return Math.sqrt(Math.pow(localizacao.getX() - destino.getX(), 2) 
                + Math.pow(localizacao.getY() - destino.getY(), 2));
    }
}
