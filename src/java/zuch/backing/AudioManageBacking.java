/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.ZConstants;
import zuch.service.AudioManagerLocal;

/**
 *
 * @author florent
 */
@Named(value = "audioManageBacking")
@ViewScoped
public class AudioManageBacking extends BaseBacking implements Serializable{
    
     @Inject AudioManagerLocal audioManager;
     
     private Audio selectedAudio;

    /**
     * Creates a new instance of AudioManageBacking
     */
    public AudioManageBacking() {
    }
    
   
    
    public String downloadAudio(Audio audio){
        
        Logger.getLogger(AudioAddBacking.class.getName()).info("CALLING DOWNLOAD AUDIO...");
        byte[] content = audio.getContent().getContent();
        
        //ExternalContext externalContext = getContext().getExternalContext();
        
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType(ZConstants.APP_AUDIO_TYPE);
        externalContext.setResponseHeader("Content-Length", String.valueOf(content.length));
        externalContext.setResponseHeader("Content-Disposition", 
                "attachment; filename=\"" 
                        + audio.getId3().getTitle()
                        + ".mp3\"");
         String lMsg = "FILE SIZE: "+  content.length;
         Logger.getLogger(AudioAddBacking.class.getName()).info(lMsg);
         OutputStream output = null;
         InputStream input = null;
         try  {
                HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
               
                output = response.getOutputStream();
                //response.setHeader("Content-Length", Long.toString(content.length));
                input = new ByteArrayInputStream(content);
               
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
            audioManager.removeAudio(selectedAudio.getId());
            String msg = "'"+selectedAudio.getId3().getTitle()+"'"
                    + " has been deleted!";
           getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
           msg, ""));
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
