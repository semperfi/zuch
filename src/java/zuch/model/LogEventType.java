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
public enum LogEventType {
    
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT");
    
    private final String value ;

    private LogEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
    
    
}
