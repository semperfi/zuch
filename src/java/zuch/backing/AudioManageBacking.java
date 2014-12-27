/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import zuch.event.EventService;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.AudioStatus;

import zuch.model.ZConstants;

import zuch.search.Indexer;
import zuch.search.ZSpellChecker;
import zuch.service.AudioManagerLocal;
import zuch.service.ZFileManager;

/**
 *
 * @author florent
 */
@Named(value = "audioManageBacking")
@ViewScoped
//@PerformanceMonitor
public class AudioManageBacking extends BaseBacking implements Serializable{
    
     @Inject AudioManagerLocal audioManager;
     @Inject JukeBoxBacking jukeBoxBacking;
     @Inject Indexer indexer;
     @Inject ZFileManager fileManager;
     @Inject ZSpellChecker spellChecker;
     @Inject EventService eventService;
     
     private Audio selectedAudio;

    /**
     * Creates a new instance of AudioManageBacking
     */
    public AudioManageBacking() {
    }
    
   
    
    public String downloadAudio(Audio audio) throws IOException{
        
        Logger.getLogger(AudioAddBacking.class.getName()).info("CALLING DOWNLOAD AUDIO...");
       // byte[] content = audio.getContent().getContent();
        InputStream audioInputStream = fileManager.getFileInputStream(audio.getFootPrint());
        
        //ExternalContext externalContext = getContext().getExternalContext();
        
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType(ZConstants.APP_AUDIO_TYPE);
        externalContext.setResponseHeader("Content-Length", String.valueOf(audioInputStream.available()));
        externalContext.setResponseHeader("Content-Disposition", 
                "attachment; filename=\"" 
                        + audio.getId3().getTitle()
                        + ".mp3\"");
         String lMsg = "FILE SIZE: "+  audioInputStream.available();
         Logger.getLogger(AudioAddBacking.class.getName()).info(lMsg);
         OutputStream output = null;
         InputStream input = null;
         try  {
                HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
               
                output = response.getOutputStream();
                //response.setHeader("Content-Length", Long.toString(content.length));
               // input = new ByteArrayInputStream(content);
                input = audioInputStream;
               
                byte[] buffer = new byte[1024*1024];
                int readLen;
                while(  (readLen = input.read(buffer)) != -1 ){
                    output.write(buffer,0,readLen);
                   
                }
                //output.write(content);
                //output.flush();
                //output.close();
                
               
            
         } catch (IOException ex) {
             Logger.getLogger(AudioManageBacking.class.getName()).log(Level.SEVERE, null, ex);
         }finally{
             getContext().responseComplete();
             if(input != null){
                 try {
                     input.close();
                 } catch (IOException ex) {
                     
                 }
             }
             if(output != null){
                 try {
                     output.flush();
                     output.close();
                 } catch (IOException ex) {
                     
                 }
             }
         }
        
        return null;        
        
    }
    
 
    
    public void deleteAudio(){
         Logger.getLogger(AudioAddBacking.class.getName()).info("CALLING DELETE AUDIO...");
        try {
            if(jukeBoxBacking.getSelectedAudio() != null){
               if(!jukeBoxBacking.getSelectedAudio().equals(selectedAudio)){
                    audioManager.removeAudio(selectedAudio.getId());
                    String msg = "'"+selectedAudio.getId3().getTitle()+"'"
                             + " has been deleted!";
                    
                    getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    msg, ""));
                    /*
                    * event received by indexer
                    */
                    eventService.getAudioDeletedEvent().fire(selectedAudio);
                   
                   
              }else{
            
                String msg = "File " + selectedAudio.getId3().getTitle() +" is used it cannot be deleted!";
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                msg, ""));
              }
            }else{
                
                audioManager.removeAudio(selectedAudio.getId());
                String msg = "'"+selectedAudio.getId3().getTitle()+"'"
                         + " has been deleted!";
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                msg, ""));
                
               eventService.getAudioDeletedEvent().fire(selectedAudio);

            }
            
            //refresh audio list to get latest tracks in view
            jukeBoxBacking.retrieveAudioList();
            
        } catch (AudioNotFound ex) {
            String msg = "File cannot be deleted!";
            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
            msg, ""));
           // Logger.getLogger(JukeBoxBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
    
  
  
   public List<Audio> retrieveManagableAudioList(){
         return audioManager.getAllUserAudiosInJukebox(getCurrentUser());
     }

    public Audio getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    
     
     
    
}
