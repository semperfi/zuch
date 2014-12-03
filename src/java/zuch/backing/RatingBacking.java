/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.backing;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import zuch.exception.UserNotFound;
import zuch.model.Audio;
import zuch.model.Rating;
import zuch.service.AudioManagerLocal;
import zuch.service.RatingManagerLocal;
import zuch.service.ZUserManagerLocal;


/**
 *
 * @author florent
 */
@Named(value = "ratingBacking")
@RequestScoped
//@PerformanceMonitor
public class RatingBacking extends BaseBacking implements Serializable{

    /**
     * Creates a new instance of RatingBacking
     */
    
    static final Logger log = Logger.getLogger("zuch.backing.RatingBacking");
    
    @Inject RatingManagerLocal ratingManager;
    @Inject ZUserManagerLocal userManager;
    @Inject AudioManagerLocal audioManager;
    @Inject JukeBoxBacking jukeBoxBacking;
    
    private int ratingValue;
    private Audio selectedAudio;
    
    public RatingBacking() {
    }
    
    public void handleRate(Audio audio) throws UserNotFound{
        
        log.info(String.format("USER RATING: %d", ratingValue));
        log.info(String.format("SELECTED AUDIO: %d", audio.getId()));
        
        Rating rating = new Rating();
        rating.setRatingValue(ratingValue);
        String currentUser = getCurrentUser();
        rating.setRatingAuthor(userManager.getZuchUser(currentUser));
        rating.setAudio(audio);
        
        ratingManager.registerRating(rating);
        audioManager.updateAudioAvgRating(audio);
        
        //refresh jukebox (playlist) to reflect rating changes
        jukeBoxBacking.retrieveAudioList();
    }
    
    public void handleCancel(){
    
    }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }
    
            
    
    
    
}
