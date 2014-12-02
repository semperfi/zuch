/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import javax.ejb.Local;

/**
 *
 * @author florent
 */
@Local
public interface EncryptionServiceLocal {
    
    String hash(String input);
    boolean compare(String hash, String input);
    byte[] stringToByteArray(String input);
    String byteArrayToHexString(byte[] input);
    
    
}
