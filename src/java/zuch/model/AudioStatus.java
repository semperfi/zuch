/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

/**
 *
 * @author florent
 */
public enum AudioStatus {
    
    LENDED("LENDED"),
    LENDING_REQUESTED("LENDING REQUESTED"),
    IN_JUKEBOX("IN JUKEBOX");
    
    
    private final String value;
    
    private AudioStatus(String value){
        
        this.value = value;
        
    }

    public String getValue() {
        return value;
    }
    
}
