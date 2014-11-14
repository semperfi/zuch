/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    CriteriaBuilder builder = em.getCriteriaBuilder();
    
        
    
   
    
    @Override
    public Rating getRating(long ratingId) throws RatingNotFound {
        CriteriaQuery<Rating> criteriaQuery = builder.createQuery(Rating.class);
        Root<Rating> c = criteriaQuery.from(Rating.class);
        
        criteriaQuery.select(c).where(builder.equal(c.get("id").as(Long.class), ratingId));
        Query query = em.createQuery(criteriaQuery);
        Rating rating = (Rating) query.getSingleResult();
        
        return rating;
    }

    @Override
    public Rating registerRating(Rating rating) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeRating(String ratingID) throws RatingNotFound {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}
