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
public class AudioAlreadyExists extends Exception{
     private final String message;
    
    public AudioAlreadyExists(){
        this.message = "Audio already exists";
    }
    
    public AudioAlreadyExists(String message){
        this.message = message;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
}
