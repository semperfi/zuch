/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.model.AudioStatus;
import zuch.service.PlayToken;
import zuch.qualifier.PerformanceMonitor;
import zuch.service.AudioManagerLocal;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@Named(value = "audioBorrowingBacking")
@RequestScoped
//@PerformanceMonitor
public class AudioBorrowingBacking extends BaseBacking implements Serializable{
    
    @Inject AudioManagerLocal audioManager;
    @Inject PlayToken playTokens;
    
    private Audio selectedAudio;
    private String audioInfo;
    
   // private int nextIndex = -1;
    
   // private List<Audio> audioList;

    /**
     * Creates a new instance of AudioBorrowingBacking
     */
    public AudioBorrowingBacking() {
    }
    
     public List<Audio> retrieveBorrowedAudioList(){
       
        return audioManager.getAllUserBorrowedAudios(getCurrentUser());
        
        
     }
     
     
      public String retrieveAudioSource(){
       
       String smsg = "SELCTED MP3: " + selectedAudio;
       Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
       
       String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedAudio != null){
            if(selectedAudio.getStatus().equals(AudioStatus.LENT)){
                currentId = selectedAudio.getId();
                
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();

               // ticketService.getAudioTicket().add(token);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);

                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id="+currentId+"&tk="+token
                        +"&rtk="+rangeToken;

                String msg = "LINK TO RETRIEVE: " + selectedAudioLink;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(msg);
            }else{
                String acceptMsg = "Hey what's this file";
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                acceptMsg, ""));
            }
           
        }else{
            
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
                
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);
                
                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        + "?id=sample"+"&tk="+token
                        +"&rtk="+rangeToken;
            
        }
        
        
        
        return selectedAudioLink;
   }
      
     
     public void retrieveAudioInfo(){
         if(selectedAudio != null){
            
                audioInfo =  selectedAudio.getId3().getArtist()+" - "+
                selectedAudio.getId3().getTitle() + " (" +
                selectedAudio.getId3().getAlbum()+" : " +
                selectedAudio.getId3().getAudioYear() + ")";
                
                String sMsg = "SELECTED AUDIO FILE: " + audioInfo;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(sMsg);
                
               // nextIndex =  audioList.indexOf(selectedAudio) + 1; //datatable indes start from 1 not 0
           
               // String nMsg = "NEXT MP3 INDEX: " + nextIndex;
               // Logger.getLogger(JukeBoxBacking.class.getName()).info(nMsg);

       }
   }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public String getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(String audioInfo) {
        this.audioInfo = audioInfo;
    }
    
     
}
