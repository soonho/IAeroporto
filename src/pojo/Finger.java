/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.io.Serializable;

/**
 *
 * @author mhayk
 */
public class Finger implements Serializable {

    private int number;
    private boolean status = false;

    public Finger(int number) {
        this.number = number;
        this.status = false;
    }

    public void Finger(int number, boolean status) {
        this.number = number;
        this.status = status;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return this.status;
    }

}
