/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.webservice;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.service.AudioManagerLocal;


/**
 *
 * @author florent
 */
@Path("audio")
@Stateless
public class AudioRestService {
    
    @Inject AudioManagerLocal audioManager;
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAudio() {
        
        //Audio audio = audioManager.getAudio((long)51);
        return "AUDIO REST END POINT";
    }
    
    
    
}
