/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import zuch.exception.AudioRequestAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.Audio;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;
import zuch.model.PlayTokens;
import zuch.model.ZUser;
import zuch.search.Searcher;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;
import zuch.service.ZUserManagerLocal;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@Named(value = "audioSearchBacking")
@ViewScoped
public class AudioSearchBacking extends BaseBacking implements Serializable{
    
    @Inject AudioManagerLocal audioManager;
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject ZUserManagerLocal userManager;
    @Inject PlayTokens playTokens;
    @Inject Searcher searcher;
  

    private String searchToken;
    private List<Audio> audioList;
    private String infoMessage;
    private Audio selectedAudio;
    private String audioInfo;
    
    
    
    public String retrieveAudioList(){
    
      if(!searchToken.isEmpty()){
         audioList = audioManager.searchForAudio(searchToken);
         searcher.searchEn(searchToken);
        // searcher.searchFr(searchToken);
        if(audioList.isEmpty()){
            infoMessage = "No audio results found";
        }else{
            infoMessage = audioList.size() + " audio file(s) found ";
        }
      }
       
        
        return null;
        
    }
    
    public void requestAudio(Audio audio){
        try {
            AudioRequest audioRequest = new AudioRequest();
                                    
            ZUser requester = userManager.getZuchUser(getCurrentUser());
            
            audioRequest.setRequester(requester);
            audioRequest.setStatus(AudioRequestStatus.PENDING);
            audioRequest.setRequestTime(System.currentTimeMillis());
            audioRequest.setRequestedAudio(audio);
            
            audio.getAudioRequests().add(audioRequest);
            audio.setStatus(AudioStatus.LENDING_REQUESTED);
            
            audioRequestManager.sendAudioRequest(audioRequest, 0);
            audioManager.updateAudio(audio);
            
            
            
            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Your request has been sent!", ""));
        } catch (AudioRequestAlreadyExists | UserNotFound ex) {
            Logger.getLogger(AudioSearchBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean showRequestLink(Audio audio){
        boolean result = false;
        if( (audio.getStatus().equals(AudioStatus.IN_JUKEBOX)) && 
            (! audio.getOwner().getId().equals(getCurrentUser())) ){
            
            result = true;
        }
        
        return result;
    }
    
    public String retrieveAudioSampleSource(){
       
       String smsg = "SELCTED MP3: " + selectedAudio;
        Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
        
        String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedAudio != null){
            currentId = selectedAudio.getId();
            
            String token = UUID.randomUUID().toString();
            String rangeToken = UUID.randomUUID().toString();
            
        
          //  ticketService.getAudioTicket().add(token);
          //  ticketService.getAudioRangeTicket().add(rangeToken);
            playTokens.setToken(token);
            playTokens.setRangeToken(rangeToken);

            selectedAudioLink =  AudioUtils.getAudioSampleStreamBaseLink()
                    +"?id="+currentId+"&tk="+token
                    +"&rtk="+rangeToken;

            String msg = "LINK TO RETRIEVE: " + selectedAudioLink;
            Logger.getLogger(JukeBoxBacking.class.getName()).info(msg);
            
        }else{
            
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
                
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);
            
                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id=sample&tk="+token
                        +"&rtk="+rangeToken;;
            
        }
        
        
        
        return selectedAudioLink;
   }
   
    public void retrieveAudioInfo(){
         if(selectedAudio != null){
                   audioInfo =  selectedAudio.getId3().getArtist()+" - "+
                   selectedAudio.getId3().getTitle() + " (" +
                   selectedAudio.getId3().getAlbum()+" : " +
                   selectedAudio.getId3().getAudioYear() + ")";
        
            String msg = "SELECTED AUDIO FILE: " + audioInfo;
            Logger.getLogger(JukeBoxBacking.class.getName()).info(msg);
           
       }
   }
    
   public String highlight(String field){
       
     
       
       return field;
   }

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }

    public String getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(String audioInfo) {
        this.audioInfo = audioInfo;
    }
    
    


    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }
    
    
}
