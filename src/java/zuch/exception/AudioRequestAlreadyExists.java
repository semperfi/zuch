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
public class AudioRequestAlreadyExists extends Exception{
    
    private final String message;
    
    public AudioRequestAlreadyExists(){
        this.message = "Request already exists";
    }
    
    public AudioRequestAlreadyExists(String message){
        this.message = message;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
    
}
