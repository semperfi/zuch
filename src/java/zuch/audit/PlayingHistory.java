/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.audit;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import zuch.model.Audio;
import zuch.qualifier.AudioPlaying;

/**
 *
 * @author florent
 */
@Stateless
public class PlayingHistory {
    
    static final Logger log = Logger.getLogger(PlayingHistory.class.getName());

    @Asynchronous
    public void OnAudioPlaying(@Observes @AudioPlaying Audio audio){
        log.info(String.format("PLAYING AUDIO USER: %s", audio.getOwner().getId()));
        log.info(String.format("PLAYING AUDIO TITLE: %s", audio.getId3().getTitle()));
        
    }
}
