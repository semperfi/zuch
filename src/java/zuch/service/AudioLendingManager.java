/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.model.AudioLending;


/**
 *
 * @author florent
 */
@Stateless
public class AudioLendingManager implements AudioLendingManagerLocal{
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;

    @Override
    public AudioLending registerLending(AudioLending lending) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endLending(AudioLending lending) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AudioLending> viewsLending() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AudioLending> getAllLendings() {
        
         Query query = em.createQuery("SELECT lending FROM AudioLending lending ");
               
        List<AudioLending> audioList = (List<AudioLending>)query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
    }

    @Override
    public AudioLending updateLending(AudioLending lending) {
        
        return em.merge(lending);
    }

    
}
