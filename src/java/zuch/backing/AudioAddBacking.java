/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;



import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import zuch.exception.AudioAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.Audio;
import zuch.model.AudioContent;
import zuch.model.AudioStatus;
import zuch.model.ID3;
import zuch.search.Content;
import zuch.search.Indexer;
import zuch.service.AudioManagerLocal;
import zuch.service.ZUserManagerLocal;
import zuch.util.AudioUtils;
import zuch.util.ZFileSystemUtils;


/**
 *
 * @author florent
 */
@Named(value = "audioAddBacking")
@RequestScoped
public class AudioAddBacking extends BaseBacking implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioAddBacking");
   // @Inject Logger log;
   // @Inject Date maintenant;
    
    
    @Inject AudioManagerLocal audioManager;
    @Inject ZUserManagerLocal userManager;
    @Inject JukeBoxBacking jukeBoxBacking;
    @Inject AudioUtils audioUtils;
    @Inject ZFileSystemUtils fileSystemUtils;
    @Inject Content searchContent;
    @Inject Indexer indexer;
    
    private Part filePart;
    
    
    
    public AudioAddBacking() {
    }
    
    public void validateFile(FacesContext ctx, UIComponent comp, Object value){
        List<FacesMessage> msgs = new ArrayList<>();
        
        Part file = (Part)value;
        
        if (file.getSize() > (30*1048576) ) {
            msgs.add(new FacesMessage("file size must not exceed 30 MB"));
            
            log.warning("file size must not exceed 30 MB");
        
        }
        if ( ! ( "audio/mpeg".equals(file.getContentType()) ||
                "audio/mp3".equals(file.getContentType()) ) ) {
             msgs.add(new FacesMessage("File format must be mp3"));
             
             log.warning("File format must be mp3");
        }
        if (! msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }
    
   
  
  private UploadedFile uploadedFile;
  
  public void validateAudioFile(FacesContext ctx, UIComponent comp, Object value){
      Logger.getLogger(AudioAddBacking.class.getName()).info("CALLING VALIDATING AUDIO...");
      
       List<FacesMessage> msgs = new ArrayList<>();
      
      if(uploadedFile != null){
        if (uploadedFile.getSize() > (30*1048576) ) {
            msgs.add(new FacesMessage("file size must not exceed 30 MB"));
            
            log.warning("file size must not exceed 30 MB");
        
        }
        if ( ! ( "audio/mpeg".equals(uploadedFile.getContentType()) ||
                "audio/mp3".equals(uploadedFile.getContentType()) ) ) {
             msgs.add(new FacesMessage("File format must be mp3"));
             
             log.warning("File format must be mp3");
        }
        if (! msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
      }
  }
  
 @Asynchronous
 public void handleFileUpload(FileUploadEvent event){
   
       
       log.fine("CALLING HANDLE FILE UPLOAD...");
      // synchronized(this){
       
         try {
                uploadedFile = event.getFile();
                
                                                
                byte[] content = IOUtils.toByteArray(uploadedFile.getInputstream());
               // byte[] content = uploadedFile.getContents();
                int sampleSize = content.length / 4;
                byte[] sample = Arrays.copyOfRange(content, 0, sampleSize);
                //System.arraycopy(content, 0, sample, 0, sample.length);
                
                Audio newAudio = new Audio();
                AudioContent newContent = new AudioContent();
                
                newContent.setContent(content);
                newContent.setContentSample(sample);
                newAudio.setContent(newContent);
                ID3 id3 = audioUtils.getID3Tag(content, uploadedFile.getFileName());
                
                
               
                if(id3.getTitle() == null){
                    id3.setTitle(fileSystemUtils.normalizeFileName(uploadedFile.getFileName()));
                }else if(id3.getTitle().isEmpty()){
                    id3.setTitle(fileSystemUtils.normalizeFileName(uploadedFile.getFileName()));
                }

                String footPrint = audioUtils.getAudioFootPrint(content);
                id3.setFootPrint(footPrint);
                newAudio.setId3(id3);
                String currentUser = getCurrentUser();
                newAudio.setOwner(userManager.getZuchUser(currentUser));
                newAudio.setStatus(AudioStatus.IN_JUKEBOX);

                Audio registredAudio = audioManager.registerAudio(newAudio);
                uploadedFile = null;
                
                indexAudioContent(registredAudio,id3);
                
            } catch ( AudioAlreadyExists | UserNotFound ex) {
                
                 log.warning("This audio file already exists in your jukebox!");
            } catch (IOException ex) {
               
               log.severe(ex.getMessage());
           }

      // }
       //refresh audio list to get latest tracks in view
       jukeBoxBacking.retrieveAudioList();
       
   }
   
   
   private void indexAudioContent(Audio audio,ID3 id3){
      // searchContent.buildContent(id3);
       indexer.buildEnIndex(audio,id3);
       indexer.buildFrIndex(audio,id3);
       indexer.buildSpIndex(audio, id3);
     
            
   }
   
    public Part getFilePart() {
        return filePart;
    }

    public void setFilePart(Part filePart) {
        this.filePart = filePart;
    }

    
    

    
    
    
}
