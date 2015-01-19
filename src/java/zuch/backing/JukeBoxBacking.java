/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;


import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import zuch.model.Audio;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;
import zuch.model.ZConstants;
import zuch.service.PlayToken;
import zuch.search.Searcher;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;
import zuch.service.ZFileManager;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@Named(value = "jukeBoxBacking")
@SessionScoped
//@PerformanceMonitor
public class JukeBoxBacking extends BaseBacking implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioAddBacking");

    @Inject AudioManagerLocal audioManager;
    @Inject PlayToken playTokens;
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject Searcher searcher;
    @Inject ZFileManager fileManager;
    
   // private List<SearchResult> searchJukeboxResultList = new ArrayList<>();
   // private String searchJukeboxToken;
    private Audio selectedAudio;
    private String audioInfo;
   // private String rangeToken;
    
    private int nextIndex = -1;
     
    private List<Audio> audioList;
    
     
   // private String currentUser;  //because we cannot get user from facescontext in predestroy
    
    @PostConstruct
    public void retrieveAudioList(){
       
       audioList = audioManager.getAllUserAudios(getCurrentUser());
       buildReceivedReqMsg();
       
     }
    
    private String searchToken;
    private String infoMessage;
            
    public String retrieveSearchedAudioList(){
    
         Logger.getLogger(JukeBoxBacking.class.getName()).info("SEARCHING IN PLAY LIST...");
         
          
        audioList = audioManager.searchForAudioInPlayList(searchToken,getCurrentUser());
        
        if(audioList.isEmpty()){
            infoMessage = "No track found";
        }else{
            infoMessage = audioList.size() + " track(s) found ";
        }
        
        getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    infoMessage, ""));
        
        Logger.getLogger(JukeBoxBacking.class.getName()).info(infoMessage);
        return null;
        
    }
   
  
   
    
  private String receivedReqMessage;
  private long receivedReqCount = 0;
  
  public void buildReceivedReqMsg(){
     
    receivedReqCount = 
            audioRequestManager.viewReceivedRequestsCount(getCurrentUser(),
                AudioRequestStatus.PENDING);
     receivedReqMessage = " - You have received "+receivedReqCount + " lending request(s).";
  }
  
  
  
   public String retrieveAudioSource(){
       
       Logger.getLogger(JukeBoxBacking.class.getName()).info("RETRIEVE AUDIO SOURCE...");
       
       String smsg = "SELCTED MP3: " + selectedAudio;
       Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
       
             
       String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedAudio != null){
            if(!selectedAudio.getStatus().equals(AudioStatus.LENT)){
                                         
                currentId = selectedAudio.getId();
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
            
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);

                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id="+currentId+"&tk="+token
                        +"&rtk="+rangeToken;

                String msg = "LINK TO RETRIEVE: " + selectedAudioLink;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(msg);
            }else{
                
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
               
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);
                
            
                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id=sample" +"&tk="+token
                        +"&rtk="+rangeToken;
                
                String acceptMsg = "You cannot play lent file";
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                acceptMsg, ""));
            }
           
        }else{
            
                String token = UUID.randomUUID().toString();
                String rangeToken = UUID.randomUUID().toString();
               
               // ticketService.getAudioTicket().add(token);
               // ticketService.getAudioRangeTicket().add(rangeToken);
                playTokens.setToken(token);
                playTokens.setRangeToken(rangeToken);
            
                selectedAudioLink =  AudioUtils.getAudioStreamBaseLink()
                        +"?id=sample" +"&tk="+token
                        +"&rtk="+rangeToken;
            
        }
        
        
        
        return selectedAudioLink;
   }
  
   //private boolean showMediaPlayer = false;
   public void retrieveAudioInfo(){
       
         if(selectedAudio != null){
                
                //showMediaPlayer = true;
                Logger.getLogger(JukeBoxBacking.class.getName()).info("SET SHOWING MEDIA PLAYER TO TRUE...");
                
                audioInfo =  selectedAudio.getId3().getArtist()+" - "+
                selectedAudio.getId3().getTitle() + " (" +
                selectedAudio.getId3().getAlbum()+" : " +
                selectedAudio.getId3().getAudioYear() + ")";
                
                String sMsg = "SELECTED AUDIO FILE: " + audioInfo;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(sMsg);
                 
                 // nextIndex =  audioList.indexOf(selectedAudio) + 1; 
                 nextIndex = (audioList.indexOf(selectedAudio)% 20) + 1;
             
                String nMsg = "NEXT MP3 INDEX: " + nextIndex;
                Logger.getLogger(JukeBoxBacking.class.getName()).info(nMsg);

       }
   }
   
   
   public StreamedContent retrieveArtWork(){
       StreamedContent image ;
       if(selectedAudio != null){
           InputStream stream = 
                   fileManager.getArtWorkFileInputStream(selectedAudio);
           image = new DefaultStreamedContent(stream, selectedAudio.getId3().getArtWorkMimeType());
       }else{
       
           InputStream stream = this.getClass().getResourceAsStream(ZConstants.ARTWORK_DEFAULT_PATH);
           image = new DefaultStreamedContent(stream, "image/png");
       }
       
       return image;
   }
   
   
   
   public boolean isPlayable(Audio audio){
       return audio.getStatus().equals(AudioStatus.LENT);
   }
   
   

    public String getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(String audioInfo) {
        this.audioInfo = audioInfo;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }
 

   

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }

    public String getReceivedReqMessage() {
        return receivedReqMessage;
    }

    public void setReceivedReqMessage(String receivedReqMessage) {
        this.receivedReqMessage = receivedReqMessage;
    }

    public long getReceivedReqCount() {
        return receivedReqCount;
    }

    public void setReceivedReqCount(long receivedReqCount) {
        this.receivedReqCount = receivedReqCount;
    }

    

   

   
}
