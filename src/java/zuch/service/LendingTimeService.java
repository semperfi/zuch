/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.model.AudioLendStatus;
import zuch.model.AudioLending;
import zuch.model.AudioStatus;

/**
 *
 * @author florent
 */
@Singleton
@Startup
public class LendingTimeService {
    
    @Inject AudioLendingManagerLocal audioLendingManager;
    @Inject AudioManagerLocal audioManager;

   //@Schedule(minute="*/5", hour="*", persistent = false)//for testing
   @Schedule(dayOfMonth="*",dayOfWeek="*",hour="0",minute="0",second="0", persistent = false)
   public void checkLendingTimeLimit(){
       
       List<AudioLending> lendings = audioLendingManager.getAllLendings();
       long now = System.currentTimeMillis();
       if(!lendings.isEmpty()){
           for(AudioLending lending : lendings){
               if(lending.getLendEnding() < now ){
                   lending.setStatus(AudioLendStatus.ENDED);
                   Audio audio = lending.getAudio();
                   audio.setStatus(AudioStatus.IN_JUKEBOX);
                   
                   audioManager.updateAudio(audio);
                   audioLendingManager.updateLending(lending);
                   
               }
           }
       }
       
   }
}
