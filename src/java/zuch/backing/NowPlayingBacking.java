/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.service.AudioManagerLocal;
import zuch.service.ZUserManagerLocal;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Named(value = "nowPlayingBacking")
@ApplicationScoped
public class NowPlayingBacking implements Serializable {

    /**
     * Creates a new instance of NowPlayingBacking
     */
    
    
    static final Logger log = Logger.getLogger("zuch.service.NowPlaying");
    
    @Inject AudioManagerLocal audioManager;
    @Inject ZUserManagerLocal userManager;
    @Inject ZFileSystemUtils fileUtils;
    
    private List<Audio> nowPlayingAudioList = new ArrayList<>();
    private HashMap<String, Audio> nowPlayingAudioMap = new HashMap<>();
    
    private TagCloudModel model;
    
     public NowPlayingBacking() {
        model = new DefaultTagCloudModel();
    }

    public void showNowPlaying(@Observes Audio audio){
          
        try {
            Audio currentAudio = audioManager.getAudio(audio.getId());
            String user = currentAudio.getOwner().getId();
            String title = currentAudio.getId3().getTitle();
            /*
            nowPlayingAudioMap.put(user, audio);
            nowPlayingAudioList.add(audio);
            
            model.addTag(new DefaultTagCloudItem(title, 2));
            */
            
            log.warning(String.format("%s FROM %s IS PLAYING...",title,user));
        } catch (AudioNotFound ex) {
            Logger.getLogger(NowPlayingBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
   /* 
   public String retrieveImagePath(Audio audio){
       
     String vPath = fileUtils.getAlbumImagePath(audio);
     Path imPath = FileSystems.getDefault().getPath(vPath);
     log.warning(String.format("IMAGE PATH %s ...",imPath.toString()));
     return imPath.toString();
       
      
   }
    */
    public List<Audio> getNowPlayingAudioList() {
        return nowPlayingAudioList;
    }

    public void setNowPlayingAudioList(List<Audio> nowPlayingAudioList) {
        this.nowPlayingAudioList = nowPlayingAudioList;
    }

    public HashMap<String, Audio> getNowPlayingAudioMap() {
        return nowPlayingAudioMap;
    }

    public void setNowPlayingAudioMap(HashMap<String, Audio> nowPlayingAudioMap) {
        this.nowPlayingAudioMap = nowPlayingAudioMap;
    }

    public TagCloudModel getModel() {
        return model;
    }

    public void setModel(TagCloudModel model) {
        this.model = model;
    }

   
    
    
    
}
