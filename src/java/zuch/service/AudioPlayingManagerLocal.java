/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.util.List;
import javax.ejb.Local;
import zuch.model.Audio;
import zuch.model.AudioPlayingInfo;

/**
 *
 * @author florent
 */
@Local
public interface AudioPlayingManagerLocal {
    
    public Audio registerAudioPlaying(AudioPlayingInfo audioInfo);
    public Audio updateAudioPlaying(AudioPlayingInfo audioInfo);
    public Audio getAudioPlaying(Long id) ;
    public void removeAudioPlaying(Long id) ;
    public List<Audio> getAllAudiosPlaying(String title);
    public List<Audio> getAllAudiosPlayingInSystem();
    public long getAllAudiosPlayingCount();
    public List<Audio> getAllUserAudiosPlaying(String userID);
    
}
