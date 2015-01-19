/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;



import java.util.ArrayList;
import java.util.List;

import java.util.OptionalDouble;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import zuch.exception.AudioAlreadyExists;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.AudioLendStatus;

import zuch.model.AudioStatus;
import zuch.model.Rating;

/**
 *
 * @author florent
 */

//@PerformanceMonitor
@Stateless
public class AudioManager implements AudioManagerLocal{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioManager");
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;

    @Inject RatingManagerLocal ratingManager;
    
    
    @Override
    public Audio registerAudio(Audio audio) throws AudioAlreadyExists {
        
       // String msg = "LINK TO RETRIEVE: " + selectedMp3Link;
        Logger.getLogger(AudioManager.class.getName()).info("REGISTERING AUDIO...");
        
       
        try{
            em.persist(audio);
            em.flush();
        }catch(PersistenceException  ex){//
            log.warning("CONSTRAINTE VIOLATION...");
            
            throw new AudioAlreadyExists();
        }   
        
        
       return audio;
    }

    @Override
    public List<Audio> getAllUserAudios(String userID) {
        
       
        TypedQuery<Audio> query = em.createNamedQuery("Audio.getAllUserAudios", Audio.class);
        
        query.setParameter("userID", userID);
       // query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        List<Audio> audioList = query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
    } 

    @Override
    public List<Audio> getAllUserAudiosInJukebox(String userID) {
        
        TypedQuery<Audio> query = em.createNamedQuery("Audio.getAllUserAudiosInJukebox",
                Audio.class);
        
        query.setParameter("userID", userID);
        query.setParameter("status", AudioStatus.IN_JUKEBOX);
        //query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        List<Audio> audioList = query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
    } 

    
    @Override
    public List<Audio> getAllAudios(String title) {
        
         Query query = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " audio.id3.title = :title");
        
        query.setParameter("title", title);
            
        List<Audio> audioList = (List<Audio>)query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
        
    }
    
    
    @Override
    public List<Audio> getAllAudiosInSystem() {
        
        Query query = em.createQuery("SELECT music FROM Audio music");
       
        List<Audio> audioList = (List<Audio>)query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
        
    }
    
    @Override
    public long getAllAudiosCount() {
         Query query = em.createQuery("SELECT COUNT(audio) FROM Audio audio ");
        
         
        long count = (Long)query.getSingleResult();
        
        return count;
    }
    
    
   
    @Override
    public List<Audio> searchForAudioInPlayList(String searchToken,String userID ) {
        
        searchToken = searchToken.toLowerCase();
        String[] tokens = searchToken.split(" ");
        
        List<Audio> result = new ArrayList<>();
        
        for(String token : tokens){
             Query titleQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + "( LOWER(audio.id3.title) LIKE :title) AND ( audio.owner.id = :userID ) "
                + " ORDER BY audio.id3.album");
        
            titleQuery.setParameter("title", "%" + token + "%");
            titleQuery.setParameter("userID", userID);
            List<Audio> tmpResult = titleQuery.getResultList();
            for(Audio audio : tmpResult){
                if(!result.contains(audio)){
                    result.add(audio);
                }
                
            }
            
            Query artistQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " (LOWER(audio.id3.artist) LIKE :artist) AND ( audio.owner.id = :userID ) "
                + " ORDER BY audio.id3.album");
        
            artistQuery.setParameter("artist", "%" + token + "%");
            artistQuery.setParameter("userID", userID);
            tmpResult = artistQuery.getResultList();
            for(Audio audio : tmpResult){
               if(!result.contains(audio)){
                   result.add(audio);
               }

            }
            
           Query albumQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " (LOWER(audio.id3.album) LIKE :album) AND ( audio.owner.id = :userID ) "
                + " ORDER BY audio.id3.album");
        
            albumQuery.setParameter("album", "%" + token + "%");
            albumQuery.setParameter("userID", userID);
            tmpResult = albumQuery.getResultList();
            for(Audio audio : tmpResult){
               if(!result.contains(audio)){
                   result.add(audio);
               }

            }
            
           Query rockQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " (LOWER(audio.id3.genre) LIKE :genre) AND ( audio.owner.id = :userID ) "
                + " ORDER BY audio.id3.album");
        
            rockQuery.setParameter("genre", "%" + token + "%");
            rockQuery.setParameter("userID", userID);
            tmpResult = rockQuery.getResultList();
            for(Audio audio : tmpResult){
               if(!result.contains(audio)){
                   result.add(audio);
               }

            }
            
        }
        
     
        return result;
    }

    
    
    @Override
    public Audio updateAudio(Audio audio) { 
       Audio updatedAudio = em.merge(audio);
       em.flush();
       log.info(String.format("UPDATED AUDIO STATUS %s", updatedAudio.getStatus()));
       return updatedAudio;
    }

    @Override
    public Audio getAudio(Long id) throws AudioNotFound{
        
        log.info(String.format("getAudio CURRENT AUDIO ID: %d", id));
        Audio audio = em.find(Audio.class, id);
        if(audio == null){
            throw new AudioNotFound();
        }
         
        return audio;
    }

    @Override
    public List<Audio> getAllUserBorrowedAudios(String userID) {
         Query query = em.createQuery("SELECT audioLending.audio FROM AudioLending"
                 + " audioLending WHERE "
                + " audioLending.tempOwner.id = :userID "
                 + " AND audioLending.status = :status");
        
        query.setParameter("userID", userID);
        query.setParameter("status", AudioLendStatus.ACTIVE);
        
        List<Audio> audioList = (List<Audio>)query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
    }

    @Override
    public void removeAudio(Long id) throws AudioNotFound {
        
        Audio audio = em.find(Audio.class, id);
        
        if(audio == null){
            throw new AudioNotFound();
        }
        
        em.remove(audio);
        em.flush();
    }

    @Override
    public Audio updateAudioAvgRating(final Audio audio) {
        List<Rating> ratingList = ratingManager.getAudioRating(audio.getId());
        
        Audio mergedAudio = null;
        if(ratingList.size() > 0){
            
          
        final OptionalDouble avg = ratingList
                    .stream()
                    .filter((rat) -> rat.getRatingValue() > 0 )
                    .mapToInt((rat) -> rat.getRatingValue())
                    .average();
           
           int intAvg = (int)avg.getAsDouble();
           Audio savedAudio = em.find(Audio.class, audio.getId());
           savedAudio.setAvgRating(intAvg);
         }
         
         return mergedAudio;
    }
    
   

    
    
}
