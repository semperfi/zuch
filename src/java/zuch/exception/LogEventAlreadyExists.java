/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author florent
 */
@ApplicationException(rollback = true)
public class LogEventAlreadyExists extends Exception{
    
    private final String message;
    
    public LogEventAlreadyExists(){
        this.message = "Log already exists";
    }
    
    public LogEventAlreadyExists(String message){
        this.message = message;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
    
}
