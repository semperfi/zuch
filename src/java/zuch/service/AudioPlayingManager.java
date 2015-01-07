/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.util.List;
import javax.ejb.Stateless;
import zuch.exception.AudioLendingAlreadyExists;
import zuch.model.Audio;
import zuch.model.AudioLending;
import zuch.model.AudioPlayingInfo;

/**
 *
 * @author florent
 */
@Stateless
public class AudioPlayingManager implements AudioPlayingManagerLocal{

    @Override
    public Audio registerAudioPlaying(AudioPlayingInfo audioInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Audio updateAudioPlaying(AudioPlayingInfo audioInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Audio getAudioPlaying(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAudioPlaying(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Audio> getAllAudiosPlaying(String title) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Audio> getAllAudiosPlayingInSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getAllAudiosPlayingCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Audio> getAllUserAudiosPlaying(String userID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

    
}
