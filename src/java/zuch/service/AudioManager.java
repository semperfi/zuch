/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBTransactionRolledbackException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
        
        Query query = em.createQuery("SELECT aud FROM Audio aud WHERE "
                + " aud.footPrint = :footprint");
        
        query.setParameter("footprint", audio.getFootPrint());
        
        try{
            query.getSingleResult();
            throw new AudioAlreadyExists();
        }catch(NoResultException exception){
            log.finer("No similar audio files found. ");
        }
        
        try{
            em.persist(audio);
            em.flush();
        }catch(PersistenceException  ex){//
            log.warning("CONSTRAINTE VIOLATION...");
            //set audio to null to check his value before saving file
            audio = null;
            Throwable t = null;
            for(t = ex.getCause(); t != null; t = t.getCause()){
                log.warning(String.format("___EXCEPTION : %s", t.getClass().toString()));
            }  
            
        }   
        
        
       return audio;
    }

    @Override
    public List<Audio> getAllUserAudios(String userID) {
        
        Query query = em.createQuery("SELECT cAudio FROM Audio cAudio WHERE "
                + " cAudio.owner.id = :userID "
                + " ORDER BY cAudio.id3.album");
        
        query.setParameter("userID", userID);
        //query.setParameter("status", AudioStatus.IN_JUKEBOX);
        query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        
        List<Audio> audioList = (List<Audio>)query.getResultList();
        
        if(audioList == null){
            return new ArrayList<>();
        }
        
        return audioList;
    } 

    @Override
    public List<Audio> getAllUserAudiosInJukebox(String userID) {
        
        Query query = em.createQuery("SELECT cAudio FROM Audio cAudio WHERE "
                + " cAudio.owner.id = :userID AND cAudio.status = :status "
                + " ORDER BY cAudio.id3.album");
        
        query.setParameter("userID", userID);
        query.setParameter("status", AudioStatus.IN_JUKEBOX);
        query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        List<Audio> audioList = (List<Audio>)query.getResultList();
        
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
    public List<Audio> searchForAudio(String searchToken) {
        
        searchToken = searchToken.toLowerCase();
        String[] tokens = searchToken.split(" ");
        
        List<Audio> result = new ArrayList<>();
        
        for(String token : tokens){
             Query titleQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " LOWER(audio.id3.title) LIKE :title");
        
            titleQuery.setParameter("title", "%" + token + "%");
            List<Audio> tmpResult = titleQuery.getResultList();
            for(Audio audio : tmpResult){
                if(!result.contains(audio)){
                    result.add(audio);
                }
                
            }
            
            Query artistQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " LOWER(audio.id3.artist) LIKE :artist");
        
            artistQuery.setParameter("artist", "%" + token + "%");
            tmpResult = artistQuery.getResultList();
            for(Audio audio : tmpResult){
               if(!result.contains(audio)){
                   result.add(audio);
               }

            }
            
           Query albumQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " LOWER(audio.id3.album) LIKE :album");
        
            albumQuery.setParameter("album", "%" + token + "%");
            tmpResult = albumQuery.getResultList();
            for(Audio audio : tmpResult){
               if(!result.contains(audio)){
                   result.add(audio);
               }

            }
            
           Query rockQuery = em.createQuery("SELECT audio FROM Audio audio WHERE "
                + " LOWER(audio.id3.genre) LIKE :genre");
        
            rockQuery.setParameter("genre", "%" + token + "%");
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
    public int updateAudioAvgRating(Audio audio) {
        List<Rating> ratingList = ratingManager.getAudioRating(audio.getId());
        int res = 0;
        if(ratingList.size() > 0){
            int sum = 0;
            for(Rating rat : ratingList){
                sum += rat.getRatingValue();
            }
            
            res = sum/ratingList.size();
            
            audio.setAvgRating(res);
            em.merge(audio);
            em.flush();
        }
        
        
        return res;
    }
    
   

    
    
}
