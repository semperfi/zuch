/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author florent
 */
@Stateless
public class EncryptionService implements EncryptionServiceLocal {

    @Override
    public String hash(final String input) {
        
        String hashPasswd = "";
        
        try {
             
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            msgDigest.update(stringToByteArray(input));
            hashPasswd = byteArrayToHexString(msgDigest.digest());
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return hashPasswd;
    }
    
    @Override
    public String hash(byte[] input) {
        
        String hashResult = "";
        
        try {
             
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            
            msgDigest.update(input);
            hashResult = byteArrayToHexString(msgDigest.digest());
            
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return hashResult;
    }


    @Override
    public boolean compare(String hash, String input) {
        return hash.equals(input);
    }

    @Override
    public byte[] stringToByteArray(String input) {
        byte[] result = null;
        try {
            result = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public String byteArrayToHexString(byte[] input) {
        
         BigInteger bigInt = new BigInteger(1, input);
         return bigInt.toString(16);
    }

    
    
}
