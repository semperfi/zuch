/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.backing;

import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import org.primefaces.event.RateEvent;
import zuch.model.Audio;
import zuch.qualifier.PerformanceMonitor;

/**
 *
 * @author florent
 */
@Named(value = "ratingBacking")
@RequestScoped
//@PerformanceMonitor
public class RatingBacking {

    /**
     * Creates a new instance of RatingBacking
     */
    
    static final Logger log = Logger.getLogger("zuch.backing.RatingBacking");
    
    private int rating;
    private Audio selectedAudio;
    
    public RatingBacking() {
    }
    
    public void handleRate(Audio audio){
        
        log.info(String.format("USER RATING: %d", rating));
        log.info(String.format("SELECTED AUDIO: %d", audio.getId()));
    }
    
    public void handleCancel(){
    
    }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }
    
            

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    
}
