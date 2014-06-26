/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.model.AudioLendStatus;
import zuch.model.AudioLending;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;
import zuch.model.ZConstants;

import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;

/**
 *
 * @author florent
 */
@Named(value = "receivedRequestsBacking")
@ViewScoped
public class ReceivedRequestsBacking extends BaseBacking implements Serializable{
    
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject AudioManagerLocal audioManager;

    
    public void ReceivedRequestsBackingg() {
    }
    
    public List<AudioRequest> retrievePendingRequests(){
        return audioRequestManager.viewReceivedRequests(getCurrentUser(),
                AudioRequestStatus.PENDING);
    }
    
    public long retrievePendingRequestsCount(){
        return audioRequestManager.viewReceivedRequestsCount(getCurrentUser(),
                AudioRequestStatus.PENDING);
    }
    
    public void acceptRequest(AudioRequest request){
        AudioLending audioLending = new AudioLending();
        
        request.setStatus(AudioRequestStatus.APPROVED);
        request.setResponseTime(System.currentTimeMillis());
        
        Audio audio = request.getRequestedAudio();
        audio.setStatus(AudioStatus.LENDED);
        //audio.getAudioRequests().add(request);
        
        audioLending.setAudio(audio);
        audioLending.setLender(audio.getOwner());
        audioLending.setTempOwner(request.getRequester());
        audioLending.setLendbegining(System.currentTimeMillis());
        long lendEnding = System.currentTimeMillis() +
                ZConstants.LENDING_LIFE_TIME * ZConstants.DAY;
        audioLending.setLendEnding(lendEnding);
        audioLending.setStatus(AudioLendStatus.ACTIVE);
        
        audio.getAudioLendings().add(audioLending);
        
        audioManager.updateAudio(audio);
        audioRequestManager.updateAudioRequest(request);
        
        String acceptMsg = request.getRequester().getId() + " request has been accepted!";
        getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    acceptMsg, ""));
        
    }
    
    public void rejectRequest(AudioRequest request){
        
        request.setStatus(AudioRequestStatus.APPROVED);
        request.setResponseTime(System.currentTimeMillis());
        
        audioRequestManager.updateAudioRequest(request);
        String acceptMsg = request.getRequester().getId() + " request has been rejected!";
        getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    acceptMsg, ""));
        
    }
    
    
    public boolean showRequestLink(AudioRequest request){
        
        boolean result = false;
        
        if(request.getStatus().equals(AudioRequestStatus.PENDING)){
            result = true;
        }
        
        return result;
    
    }
}
