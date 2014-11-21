/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.exception.RatingNotFound;
import zuch.model.Rating;

/**
 *
 * @author florent
 */

 // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
/*
use creteria api here just for trying this api
*/

@Stateless
public class RatingManager implements RatingManagerLocal {
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;
    
    
   
    @Override
    public Rating getRating(long ratingId) throws RatingNotFound {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rating registerRating(Rating rating) {
       
       em.persist(rating);
       em.flush();
       
      
       return rating;
    }

    @Override
    public void removeRating(String ratingID) throws RatingNotFound {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Rating> getAudioRating(long audioId) {
         Query query = em.createQuery("SELECT audioRatings FROM Rating audioRatings WHERE "
                + " audioRatings.audio.id = :audioId ");
        
        query.setParameter("audioId", audioId);
        //query.setParameter("status", AudioStatus.IN_JUKEBOX);
        query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        
        List<Rating> ratingList = (List<Rating>)query.getResultList();
        
        if(ratingList == null){
            return new ArrayList<>();
        }
        
        return ratingList;
    }

   
    
}
