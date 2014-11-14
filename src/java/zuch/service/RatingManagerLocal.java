/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import javax.ejb.Local;
import zuch.exception.RatingNotFound;
import zuch.model.Rating;

/**
 *
 * @author florent
 */
@Local
public interface RatingManagerLocal {
     public Rating getRating(long ratingId) throws RatingNotFound;
    
    public Rating registerRating(Rating rating) ;
    public void removeRating(String ratingID) throws RatingNotFound;
    
}
