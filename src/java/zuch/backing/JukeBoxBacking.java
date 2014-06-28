/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;

import java.util.ArrayList;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;

import javax.inject.Inject;

import zuch.model.Audio;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;
import zuch.model.PlayTokens;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManager;
import zuch.service.AudioRequestManagerLocal;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@Named(value = "jukeBoxBacking")
@SessionScoped
public class JukeBoxBacking extends BaseBacking implements Serializable{

    @Inject AudioManagerLocal audioManager;
    @Inject PlayTokens playTokens;
    @Inject AudioRequestManagerLocal audioRequestManager;
    
    
    private Audio selectedAudio;
    private String audioInfo;
   // private String rangeToken;
    
    private int nextIndex = -1;
    
    
    
    private List<Audio> audioList;
    
    
    @PostConstruct
    public void retrieveAudioList(){
       
       audioList = audioManager.getAllUserAudios(getCurrentUser());
       buildReceivedReqMsg();
     }
    
    
    private String searchToken;
    private String infoMessage;
            
    public String retrieveSearchedAudioList(){
    
         Logger.getLogger(JukeBoxBacking.class.getName()).info("SEARCHING IN PLAY LIST...");
        
        audioList = audioManager.searchForAudioInPlayList(searchToken,getCurrentUser());
        
        if(audioList.isEmpty()){
            infoMessage = "No track found";
        }else{
            infoMessage = audioList.size() + " track(s) found ";
        }
        
        getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    infoMessage, ""));
        
        Logger.getLogger(JukeBoxBacking.class.getName()).info(infoMessage);
        return null;
        
    }
   
  private String receivedReqMessage;
  private long receivedReqCount;
  
  public void buildReceivedReqMsg(){
     
    receivedReqCount = 
            audioRequestManager.viewReceivedRequestsCount(getCurrentUser(),
                AudioRequestStatus.PENDING);
     receivedReqMessage = "You have "+receivedReqCount + " lending request(s).";
  }
  
   public String retrieveAudioSource(){
       
       Logger.getLogger(JukeBoxBacking.class.getName()).info("RETRIEVE AUDIO SOURCE...");
       
       String smsg = "SELCTED MP3: " + selectedAudio;
       Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
       
       String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedAudio != null){
            if(!selectedAudio.getStatus().equals(AudioStatus.LENT)){
                                         
                currentId = selectedAudio.getId();
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
            
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);

                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
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
                        +"?id=sample" +"&tk="+token
                        +"&rtk="+rangeToken;
                
                String acceptMsg = "You cannot play lended file";
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                acceptMsg, ""));
            }
           
        }else{
            
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
               
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);
            
                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id=sample" +"&tk="+token
                        +"&rtk="+rangeToken;
            
        }
        
        
        
        return selectedAudioLink;
   }
  
   //private boolean showMediaPlayer = false;
   public void retrieveAudioInfo(){
       
         if(selectedAudio != null){
                
                //showMediaPlayer = true;
                Logger.getLogger(JukeBoxBacking.class.getName()).info("SET SHOWING MEDIA PLAYER TO TRUE...");
                
                audioInfo =  selectedAudio.getId3().getArtist()+" - "+
                selectedAudio.getId3().getTitle() + " (" +
                selectedAudio.getId3().getAlbum()+" : " +
                selectedAudio.getId3().getAudioYear() + ")";
                
                String sMsg = "SELECTED AUDIO FILE: " + audioInfo;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(sMsg);
                
               
                
                 // nextIndex =  audioList.indexOf(selectedAudio) + 1; 
                 nextIndex = (audioList.indexOf(selectedAudio)% 20) + 1;
                
               
           
                String nMsg = "NEXT MP3 INDEX: " + nextIndex;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(nMsg);

       }
   }
   
   public boolean isPlayable(Audio audio){
       return audio.getStatus().equals(AudioStatus.LENT);
   }
   
   public void download(){
       Logger.getLogger(JukeBoxBacking.class.getName()).info("DOWNLOADING FILE...");
   }
   
   
    public void downloadAudio(Audio audio){

    }

    public void removeAudio(Audio audio){

    }

    public String getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(String audioInfo) {
        this.audioInfo = audioInfo;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }
 

   

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }

    public String getReceivedReqMessage() {
        return receivedReqMessage;
    }

    public void setReceivedReqMessage(String receivedReqMessage) {
        this.receivedReqMessage = receivedReqMessage;
    }

    public long getReceivedReqCount() {
        return receivedReqCount;
    }

    public void setReceivedReqCount(long receivedReqCount) {
        this.receivedReqCount = receivedReqCount;
    }

    

   

   
}
