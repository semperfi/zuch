/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import javax.inject.Inject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import zuch.exception.UserNotFound;
import zuch.model.Audio;
import zuch.model.Rating;
import zuch.model.ZConstants;
import zuch.service.AudioManagerLocal;
import zuch.service.RatingManagerLocal;
import zuch.service.ZFileManager;
import zuch.service.ZUserManagerLocal;


/**
 *
 * @author florent
 */
@Named(value = "ratingBacking")
@SessionScoped
//@PerformanceMonitor
public class RatingBacking extends BaseBacking implements Serializable{

    /**
     * Creates a new instance of RatingBacking
     */
    
    static final Logger log = Logger.getLogger(RatingBacking.class.getName());
    
    @Inject RatingManagerLocal ratingManager;
    @Inject ZUserManagerLocal userManager;
    @Inject AudioManagerLocal audioManager;
    @Inject JukeBoxBacking jukeBoxBacking;
    @Inject ZFileManager fileManager;
    
    private int ratingValue;
    private String comment;
    private Audio selectedAudio;
    
    public RatingBacking() {
    }
    
    /*
    public void handleRate(Audio audio) throws UserNotFound{
        
        log.info(String.format("USER RATING: %d", ratingValue));
        log.info(String.format("SELECTED AUDIO: %d", audio.getId()));
        
        Rating rating = new Rating();
        rating.setRatingValue(ratingValue);
        rating.setComment(comment);
        String currentUser = getCurrentUser();
        rating.setRatingAuthor(userManager.getZuchUser(currentUser));
        rating.setAudio(audio);

        ratingManager.registerRating(rating);
        audioManager.updateAudioAvgRating(audio);

        //refresh jukebox (playlist) to reflect rating changes
        jukeBoxBacking.retrieveAudioList();
       
        
    }
    */
    
    public void handleCancel(){
    
    }
    
    
    public void saveRating() throws UserNotFound{
        
        log.info(String.format("USER RATING: %d", ratingValue));
       
        if(selectedAudio != null){
             log.info(String.format("SELECTED AUDIO: %d", selectedAudio.getId()));
       
            Rating rating = new Rating();
            rating.setRatingValue(ratingValue);
            rating.setComment(comment);
            String currentUser = getCurrentUser();
            rating.setRatingAuthor(userManager.getZuchUser(currentUser));
            rating.setAudio(selectedAudio);

            ratingManager.registerRating(rating);
            audioManager.updateAudioAvgRating(selectedAudio);
            
            ratingValue = 0;
            comment = "";
            //selectedAudio = registeredAudio;

            //refresh jukebox (playlist) to reflect rating changes
            jukeBoxBacking.retrieveAudioList();
        }
       
       
        
    }
    
    public void handleClose(){
        log.info("CLEANING BEAN ON CLOSING...");
        
        ratingValue = 0;
        comment = "";
        selectedAudio = null;
        
    }
    
    public StreamedContent retrieveArtWork(){
       StreamedContent image ;
       if(selectedAudio != null){
           log.info(String.format("SELECTED AUDIO TITLE %s", selectedAudio.getId3().getTitle()));
           InputStream stream = 
                   fileManager.getArtWorkFileInputStream(selectedAudio);
           image = new DefaultStreamedContent(stream, selectedAudio.getId3().getArtWorkMimeType());
       }else{
       
           InputStream stream = this.getClass().getResourceAsStream(ZConstants.ARTWORK_DEFAULT_PATH);
           image = new DefaultStreamedContent(stream, "image/png");
       }
       
       return image;
   }
    
   public List<Rating> retrieveRatings(){
       if(selectedAudio != null){
           return ratingManager.getAudioRating(selectedAudio.getId());
       }else{
           return new ArrayList<>();
       }
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
            
    
    
    
}
