/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import zuch.exception.AudioAlreadyExists;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;

/**
 *
 * @author florent
 */
@Local
public interface AudioManagerLocal {
    
  
    public Audio registerAudio(Audio audio) throws AudioAlreadyExists;
    public Audio updateAudio(Audio audio);
    public Audio getAudio(Long id) throws AudioNotFound;
    public void removeAudio(Long id) throws AudioNotFound;
    public List<Audio> getAllAudios(String title);
    public List<Audio> getAllAudiosInSystem();
    public long getAllAudiosCount();
    public List<Audio> getAllUserAudios(String userID);
    public List<Audio> getAllUserAudiosInJukebox(String userID);
    public List<Audio> getAllUserBorrowedAudios(String userID);
    public List<Audio> searchForAudio(String searchToken);
    
    public List<Audio> searchForAudioInPlayList(String searchToken, String userID);
    
}
