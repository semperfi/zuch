/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.exception.AudioAlreadyExists;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.AudioLendStatus;
import zuch.model.AudioStatus;

/**
 *
 * @author florent
 */
@Stateless
public class AudioManager implements AudioManagerLocal{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioManager");
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;

    @Interceptors(LoggingInterceptor.class)
    @Override
    public Audio registerAudio(Audio audio) throws AudioAlreadyExists {
        
       // String msg = "LINK TO RETRIEVE: " + selectedMp3Link;
        Logger.getLogger(AudioManager.class.getName()).info("CALLING REGISTER AUDIO...");
        
        Query query = em.createQuery("SELECT aud FROM Audio aud WHERE "
                            + "aud.id3.footPrint = :footPrint");
        
        query.setParameter("footPrint", audio.getId3().getFootPrint());
        
        try{
            query.getSingleResult();
            throw new AudioAlreadyExists();
        }catch(NoResultException ex){
           
            log.fine("No similar audio file found");
        }
        
        try{
            
            em.persist(audio);
            em.flush();
            em.clear();
            //String msg = "LINK TO RETRIEVE: " + selectedMp3Link;
            
        }catch(Exception ex){
            
            log.severe(ex.getMessage());
        }
        return audio;
    }

    @Override
    public List<Audio> getAllUserAudios(String userID) {
        
        Query query = em.createQuery("SELECT cAudio FROM Audio cAudio WHERE "
                + " cAudio.owner.id = :userID "
                + " ORDER BY cAudio.id3.album");
        
        query.setParameter("userID", userID);
      //  query.setParameter("status", AudioStatus.IN_JUKEBOX);
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
       return em.merge(audio);
    }

    @Override
    public Audio getAudio(Long id) throws AudioNotFound{
        
        /*
        Query query = em.createQuery("SELECT audio FROM Audio audio WHERE "
                            + "audio.id = :id");
        query.setParameter("id", id);
        
        Audio audio = null;
        
        try{
            audio = (Audio)query.getSingleResult();
        }catch(NoResultException ex){
            log.warning(ex.getMessage());
        }
        */
        
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
    
    /*
    @AroundInvoke
    private Object logMethod(InvocationContext ic) throws Exception {
                
        String enterMessage = "["+ic.getTarget().toString() 
                +"] ENTER "+ic.getMethod().getName();
        log.info(enterMessage);
        
    try {
        return ic.proceed();
    } finally {
        String exitMessage = "["+ic.getTarget().toString() 
                +"] EXIT "+ic.getMethod().getName();
        log.info(exitMessage);
    }
    }
    */
    
}
