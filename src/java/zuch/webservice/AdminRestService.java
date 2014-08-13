/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.webservice;

import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author florent
 */
@Path("admin")
@Stateless
public class AdminRestService {
    
    public String deleteIndex(){
    
        return "deleting index...";
    }
    
    public String rebuildIndex(){
        return "rebuild index...";
    }
    
    public String rebuildSpellCheckerIndex(){
        return "rebuild spell checker index...";
    }
    
}
