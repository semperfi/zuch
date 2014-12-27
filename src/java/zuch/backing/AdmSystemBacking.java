/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.backing;

import java.util.List;
import javax.inject.Named;

import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import zuch.model.Audio;

import zuch.service.AudioManagerLocal;
import zuch.event.EventService;

/**
 *
 * @author florent
 */
@Named(value = "admSystemBacking")
@RequestScoped
public class AdmSystemBacking {

    /**
     * Creates a new instance of AdmSystemBacking
     */
    
    @Inject EventService eventService;
      
   @Inject  
   private  AudioManagerLocal audioManager;
    
    public AdmSystemBacking() {
    }
    
    public void rebuildIndex(){
       List<Audio> musicList = audioManager.getAllAudiosInSystem();
       for(Audio audio : musicList){
           eventService.getAudioAddedEvent().fire(audio);
       }
       
    }
    
   
    
    
}
