/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.lucene.store.RAMDirectory;
import zuch.exception.AudioNotFound;
import zuch.exception.AudioRequestAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.Audio;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.model.AudioStatus;
import zuch.model.PlayTokens;
import zuch.model.SearchResult;
import zuch.model.ZUser;
import zuch.search.SearchUtils;
import zuch.search.Searcher;
import zuch.search.Suggest;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;
import zuch.service.ZUserManagerLocal;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@Named(value = "audioSearchBacking")
@ViewScoped
public class AudioSearchBacking extends BaseBacking implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioSearchBacking");
    
    @Inject AudioManagerLocal audioManager;
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject ZUserManagerLocal userManager;
    @Inject PlayTokens playTokens;
    @Inject Searcher searcher;
    @Inject Suggest suggest;
    @Inject SearchUtils searchUtils;
  

    private String searchToken;
    
    private List<Audio> audioList;
    private List<SearchResult> searchResultList = new ArrayList<>();
    
   // private List<String> sugestionResultList = new ArrayList<>();
    private String currentSuggestion = "";
    private String infoMessage;
    private Audio selectedAudio;
    private SearchResult selectedResult;
    private String audioInfo;
    
    
    
    public String retrieveLuceneSearchAudios(){
        if(searchUtils.isSearchTokenValid(searchToken)){
         
          searchResultList = searcher.luceneSearchForAudio(searchToken);
          currentSuggestion  = suggest.buildSuggestions(searchToken);
          infoMessage = searchResultList.size() + " audio file(s) found ";             
          
        
      }else{
            infoMessage = "No results found";
      }
       
       
        return null;
    }
    
   
    
    
   public String luceneSuggestAudios(){
       if(!currentSuggestion.isEmpty()){
         
          log.info("SUBMITING SUGGESTION....");
          searchResultList = searcher.luceneSearchForAudio(currentSuggestion);
          
          currentSuggestion = "";
          
       
          if(searchResultList.isEmpty()){
               infoMessage = "No results found";
          }else{
               infoMessage = searchResultList.size() + " audio file(s) found ";
               
          }
        
      }
       
        
        return null;
   }
    
   public boolean showSuggestion(){
   
       boolean result = false;
       if( (searchResultList.isEmpty()) && (!currentSuggestion.isEmpty()) ){
           result = true;
       }
       
       return result;
   }
    
   
   
    
    public void requestAudio(Audio audio){
        try {
            AudioRequest audioRequest = new AudioRequest();
                                    
            ZUser requester = userManager.getZuchUser(getCurrentUser());
            
            audioRequest.setRequester(requester);
            audioRequest.setStatus(AudioRequestStatus.PENDING);
            audioRequest.setRequestTime(System.currentTimeMillis());
            audioRequest.setRequestedAudio(audio);
            
            audio.getAudioRequests().add(audioRequest);
            audio.setStatus(AudioStatus.LENDING_REQUESTED);
            
            audioRequestManager.sendAudioRequest(audioRequest, 0);
            audioManager.updateAudio(audio);
            
            
            
            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Your request has been sent!", ""));
        } catch (AudioRequestAlreadyExists | UserNotFound ex) {
            Logger.getLogger(AudioSearchBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void requestAudioLending(SearchResult searchResult){
        try {
            
            Audio audio = audioManager.getAudio(searchResult.getAudioId());
            AudioRequest audioRequest = new AudioRequest();
                                    
            ZUser requester = userManager.getZuchUser(getCurrentUser());
            
            audioRequest.setRequester(requester);
            audioRequest.setStatus(AudioRequestStatus.PENDING);
            audioRequest.setRequestTime(System.currentTimeMillis());
            audioRequest.setRequestedAudio(audio);
            
            audio.getAudioRequests().add(audioRequest);
            audio.setStatus(AudioStatus.LENDING_REQUESTED);
            
            audioRequestManager.sendAudioRequest(audioRequest, 0);
            audioManager.updateAudio(audio);
            
            
            
            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Your request has been sent!", ""));
        } catch (AudioRequestAlreadyExists | UserNotFound | AudioNotFound ex) {
            Logger.getLogger(AudioSearchBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean showRequestLink(Audio audio){
        boolean result = false;
        if( (audio.getStatus().equals(AudioStatus.IN_JUKEBOX)) && 
            (! audio.getOwner().getId().equals(getCurrentUser())) ){
            
            result = true;
        }
        
        return result;
    }
    
    public boolean showAudioRequestLink(SearchResult searchResult){
        boolean result = false;
        try {
            
            
            Audio audio = audioManager.getAudio(searchResult.getAudioId());
            if( (audio.getStatus().equals(AudioStatus.IN_JUKEBOX)) && 
            (! audio.getOwner().getId().equals(getCurrentUser())) ){
            
                result = true;
            }
        
        } catch (AudioNotFound ex) {
            Logger.getLogger(AudioSearchBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public String retrieveAudioSampleSource(){
       
       String smsg = "SELCTED MP3: " + selectedAudio;
        Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
        
        String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedAudio != null){
            currentId = selectedAudio.getId();
            
            String token = UUID.randomUUID().toString();
            String rangeToken = UUID.randomUUID().toString();
            
        
          //  ticketService.getAudioTicket().add(token);
          //  ticketService.getAudioRangeTicket().add(rangeToken);
            playTokens.setToken(token);
            playTokens.setRangeToken(rangeToken);

            selectedAudioLink =  AudioUtils.getAudioSampleStreamBaseLink()
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
                        +"?id=sample&tk="+token
                        +"&rtk="+rangeToken;;
            
        }
        
        
        
        return selectedAudioLink;
   }
   
    public String retrieveAudioResultSampleSource(){
       
       String smsg = "SELCTED MP3: " + selectedResult;
        Logger.getLogger(JukeBoxBacking.class.getName()).info(smsg);
        
        String selectedAudioLink = "";
       
        long currentId = -1;
        
        if(selectedResult != null){
            currentId = selectedResult.getAudioId();
            
            String token = UUID.randomUUID().toString();
            String rangeToken = UUID.randomUUID().toString();
            
        
          //  ticketService.getAudioTicket().add(token);
          //  ticketService.getAudioRangeTicket().add(rangeToken);
            playTokens.setToken(token);
            playTokens.setRangeToken(rangeToken);

            selectedAudioLink =  AudioUtils.getAudioSampleStreamBaseLink()
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
                        +"?id=sample&tk="+token
                        +"&rtk="+rangeToken;;
            
        }
        
        return selectedAudioLink;
   }
   
    
    
    public void retrieveAudioInfo(){
        
         
         if(selectedResult != null){
                   audioInfo =  selectedResult.getArtist()+" - "+
                   selectedResult.getTitle() + " (" +
                   selectedResult.getAlbum()+" : " +
                   selectedResult.getAudioYear() + ")";
        
            String msg = "SELECTED AUDIO FILE IN SEARCH BACKING: " + audioInfo;
            log.info(msg);
           
       }
         
       
   }
    
   
  

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }

    public String getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(String audioInfo) {
        this.audioInfo = audioInfo;
    }

    public List<SearchResult> getSearchResultList() {
        return searchResultList;
    }
    
    


    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public SearchResult getSelectedResult() {
        return selectedResult;
    }

    public void setSelectedResult(SearchResult selectedResult) {
        this.selectedResult = selectedResult;
    }

    

    public String getCurrentSuggestion() {
        return currentSuggestion;
    }

    public void setCurrentSuggestion(String currentSuggestion) {
        this.currentSuggestion = currentSuggestion;
    }

    
    
    
}
