/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;


import javax.enterprise.context.RequestScoped;

/**
 *
 * @author florent
 */
@RequestScoped
public class SearchUtils {

   public boolean isSearchTokenValid(String token){
        boolean result = true;
        
        if(token.isEmpty()){
            result = false;
        }
        if(token.length() < 2){
            result = false;
        }
        
        return result;
    }
}
