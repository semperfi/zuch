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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.exception.AudioRequestAlreadyExists;
import zuch.exception.AudioRequestNotFound;
import zuch.model.Audio;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;

/**
 *
 * @author florent
 */
@Stateless
public class AudioRequestManager implements AudioRequestManagerLocal{
    
    @PersistenceContext(unitName = "ZuchPU")
    EntityManager em;

    @Override
    public AudioRequest sendAudioRequest(AudioRequest audioRequest, 
            int lendLifeTime) throws AudioRequestAlreadyExists {
        Query query = em.createQuery("SELECT audioRequest FROM AudioRequest audioRequest "
                + " WHERE audioRequest.id = :id ");
        
        query.setParameter("id", audioRequest.getId());
       // query.setParameter("zuserId", audioRequest.getRequester().getId());
        
        try{
            query.getSingleResult();
            throw new AudioRequestAlreadyExists();
        }catch(NoResultException ex){
           // Logger.getLogger(AudioRequestManager.class.getName()).log(Logger.Level.INFO, "No request found");
            System.out.println("No request found");
        
        }
        
        audioRequest.setRequestTime(System.currentTimeMillis());
        audioRequest.setStatus(AudioRequestStatus.PENDING);
        audioRequest.setLendLifeTime(lendLifeTime);
        
        em.persist(audioRequest);
        em.flush();
        
        return audioRequest;
        
        
    }

    @Override
    public void approveAudioRequest(AudioRequest audioRequest) throws AudioRequestNotFound {
        
        AudioRequest updatableAudiorequest = em.find(AudioRequest.class, audioRequest.getId());
        
        if(updatableAudiorequest == null){
            throw new AudioRequestNotFound();
        }
        
        updatableAudiorequest.setStatus(AudioRequestStatus.APPROVED);
        updatableAudiorequest.setResponseTime(System.currentTimeMillis());
        
        //set audio status
        Audio updatableAudio = updatableAudiorequest.getRequestedAudio();
        updatableAudio.setStatus(AudioStatus.LENT);
        
        em.merge(updatableAudio);
        em.flush();
    }

    @Override
    public void rejectAudioRequest(AudioRequest audioRequest) throws AudioRequestNotFound {
        AudioRequest updatableAudiorequest = em.find(AudioRequest.class, audioRequest.getId());
        
        if(updatableAudiorequest == null){
            throw new AudioRequestNotFound();
        }
        
        updatableAudiorequest.setStatus(AudioRequestStatus.REJECTED);
        updatableAudiorequest.setResponseTime(System.currentTimeMillis());
        
        //set audio status
        Audio updatableAudio = updatableAudiorequest.getRequestedAudio();
        //updatableAudio.setStatus(AudioStatus.LENDED);
        
        em.merge(updatableAudio);
        em.flush();
    }

    @Override
    public List<AudioRequest> viewReceivedRequests(String zuserName, AudioRequestStatus status) {
        Query query = em.createQuery("SELECT audioRequest FROM AudioRequest audioRequest "
                + " WHERE audioRequest.requestedAudio.owner.id = :id AND "
                + " audioRequest.status = :status");
        
        query.setParameter("id", zuserName);
        query.setParameter("status", status);
        
        List<AudioRequest> requests = query.getResultList();
        if(requests == null){
            requests = new ArrayList<>();
        }
        
        return requests;
        
    }
    
    @Override
    public long viewReceivedRequestsCount(String zuserName, AudioRequestStatus status) {
        Query query = em.createQuery("SELECT COUNT(audioRequest) FROM AudioRequest audioRequest "
                + " WHERE audioRequest.requestedAudio.owner.id = :id AND "
                + " audioRequest.status = :status");
        
        query.setParameter("id", zuserName);
        query.setParameter("status", status);
        
        long result = (long)query.getSingleResult();
        
        return result;
        
    }
    
    
    @Override
    public long viewSentRequestsCount(String zuserName, AudioRequestStatus status) {
        Query query = em.createQuery("SELECT COUNT(audioRequest) FROM AudioRequest audioRequest "
                + " WHERE audioRequest.requestedAudio.owner.id = :id AND "
                + " audioRequest.status = :status");
        
        query.setParameter("id", zuserName);
        query.setParameter("status", status);
        
        long result = (long)query.getSingleResult();
        
        return result;
        
    }
    
    
     @Override
    public List<AudioRequest> viewSentRequests(String zuserName, AudioRequestStatus status) {
        Query query = em.createQuery("SELECT audioRequest FROM AudioRequest audioRequest "
                + " WHERE audioRequest.requester.id = :id AND "
                + " audioRequest.status = :status");
        
        query.setParameter("id", zuserName);
        query.setParameter("status", status);
        
        List<AudioRequest> requests = query.getResultList();
        if(requests == null){
            requests = new ArrayList<>();
        }
        
        return requests;
        
    }

    @Override
    public AudioRequest updateAudioRequest(AudioRequest audioRequest) {
       return em.merge(audioRequest);
    }

    
}
