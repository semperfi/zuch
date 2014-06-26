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
public enum ZGroupRole {
    
    ZUCH_USER("ZUCH_USER"),
    ZUCH_ADMIN("ZUCH_ADMIN");
    
    
    private final String value;
    
    private ZGroupRole(String value){
        
        this.value = value;
        
    }

    public String getValue() {
        return value;
    }
    
    
    
}
