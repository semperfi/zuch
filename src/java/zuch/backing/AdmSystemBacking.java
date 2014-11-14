/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.backing;

import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.qualifier.AudioRebuilt;
import zuch.service.AudioManagerLocal;

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
    
   @Inject
   private @AudioRebuilt Event<Audio> rebuidSearchIndexEvent;
   
   @Inject  AudioManagerLocal audioManager;
    
    public AdmSystemBacking() {
    }
    
    public void rebuildIndex(){
       List<Audio> musicList = audioManager.getAllAudiosInSystem();
       for(Audio audio : musicList){
           rebuidSearchIndexEvent.fire(audio);
       }
       
    }
}
