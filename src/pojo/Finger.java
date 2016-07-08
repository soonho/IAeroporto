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
    private Integer number;
    private Boolean status = false;
    
    public void setFingerNumber(Integer number) 
    {
        this.number = number;
    }
    
    public Integer getFingerNumber()
    {
        return this.number;
    }
    
    public void setFingerStatus(Boolean status)
    {
        this.status = status;
    }
    
    public Boolean getFingerStatus()
    {
        return this.status;
    }
    
}
