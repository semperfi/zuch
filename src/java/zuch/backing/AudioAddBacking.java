/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;



import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;


import zuch.exception.AudioAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.Audio;

import zuch.model.AudioStatus;
import zuch.model.ID3;

import zuch.search.Indexer;
import zuch.search.ZSpellChecker;
import zuch.service.AudioManagerLocal;
import zuch.event.EventService;
import zuch.event.AudioAddedPayload;
import zuch.service.AudioManager;

import zuch.service.ZFileManager;
import zuch.service.ZUserManagerLocal;
import zuch.util.AudioUtils;
import zuch.util.ZFileSystemUtils;


/**
 *
 * @author florent
 */
@Named(value = "audioAddBacking")
@RequestScoped
//@PerformanceMonitor
public class AudioAddBacking extends BaseBacking implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.service.AudioAddBacking");
   // @Inject Logger log;
   // @Inject Date maintenant;
    
    
    
    @Inject AudioManagerLocal audioManager;
    @Inject ZUserManagerLocal userManager;
    @Inject JukeBoxBacking jukeBoxBacking;
    @Inject AudioUtils audioUtils;
    @Inject ZFileSystemUtils fileSystemUtils;
    @Inject Indexer indexer;
    @Inject ZSpellChecker spellChecker;
    @Inject ZFileManager fileManager;
    @Inject EventService eventService;
    
    
 
  @Asynchronous
  public void handleFileUpload(final FileUploadEvent event){
   
       
       log.info("CALLING HANDLE FILE UPLOAD...");
      // synchronized(this){
       
       log.info(String.format("METHOD handleFileUpload(FileUploadEvent event) ON THREAD [%s]", 
                Thread.currentThread().getName()));   
           
       
       try {
                UploadedFile  uploadedFile = event.getFile();
                
                if(!isValid(uploadedFile)){
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Error", "Invalid file format or size.");
                    FacesContext.getCurrentInstance()
                            .addMessage(null, msg );
                    throw new ValidatorException(msg);
                }
                
                                                
                byte[] content = IOUtils.toByteArray(uploadedFile.getInputstream());
               
                Audio newAudio = new Audio();
               
                ID3 id3 = audioUtils.getID3Tag(content, uploadedFile.getFileName());
               
                if(id3.getTitle() == null){
                    id3.setTitle(fileSystemUtils.normalizeFileName(uploadedFile.getFileName()));
                }else if(id3.getTitle().isEmpty()){
                    id3.setTitle(fileSystemUtils.normalizeFileName(uploadedFile.getFileName()));
                }
                
                String userId = getCurrentUser();

                String footPrint = audioUtils.getAudioFootPrint(content,userId);
                newAudio.setFootPrint(footPrint);
                newAudio.setId3(id3);
                String currentUser = getCurrentUser();
                newAudio.setOwner(userManager.getZuchUser(currentUser));
                newAudio.setStatus(AudioStatus.IN_JUKEBOX);

                Audio registredAudio = audioManager.registerAudio(newAudio);
               // uploadedFile = null;
                if(registredAudio != null){
                   
                    /*
                    * event received by lucene indexer, 
                    * spell checker and artwork image saving
                    */
                    eventService.getAudioAddedEvent().fire(registredAudio);
                    /*
                    * save file on physical disk
                    * event received by zuch.service.ZFileManager
                    */
                    AudioAddedPayload payload = new AudioAddedPayload(content, footPrint);
                    eventService.getContentReceivedEvent().fire(payload);
                }
               
                
            }catch ( UserNotFound | AudioAlreadyExists ex) {
                
               log.warning(ex.getMessage());
               
            }catch (IOException ex) {
               
               log.severe(ex.getMessage());
               
           }catch(ValidatorException ex){
               log.warning("Invalid file format or size.");
           }


      
       //refresh audio list to get latest tracks in view
       jukeBoxBacking.retrieveAudioList();
       
   }
 
   
   private boolean isValid(UploadedFile upFile){
     boolean result = true;
     if(upFile != null){
        if (upFile.getSize() > (30*1048576) ) {
            
            log.warning("file size must not exceed 30 MB");
            result = false;
        
        }
        if ( ! ( "audio/mpeg".equals(upFile.getContentType()) ||
                "audio/mp3".equals(upFile.getContentType()) ) ) {
             
             log.warning("File format must be mp3");
             result = false;
        }
        
      }
     
     return result;
    }
    
    
}
