/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.ZUser;

/**
 *
 * @author florent
 */
@Singleton
public class NowPlaying {
    static final Logger log = Logger.getLogger("zuch.service.NowPlaying");
    @Inject AudioManagerLocal audioManager;
    @Inject ZUserManagerLocal userManager;

    public void showNowPlaying(@Observes Audio audio){
        try {
            Audio currentAudio = audioManager.getAudio(audio.getId());
            String user = currentAudio.getOwner().getId();
            String title = currentAudio.getId3().getTitle();
            log.warning(String.format("%s FROM %s IS PLAYING...",title,user));
        } catch (AudioNotFound ex) {
            Logger.getLogger(NowPlaying.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
