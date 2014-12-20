/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import zuch.model.Audio;

import zuch.model.ID3;
import zuch.model.ZConstants;
import zuch.qualifier.Added;
import zuch.service.EncryptionServiceLocal;
import zuch.service.ZFileManager;

/**
 *
 * @author florent
 */
//@Stateless
@RequestScoped
public class AudioUtils {
    
    static final Logger log = Logger.getLogger("zuch.service.AudioUtils");
    
    @Inject ZFileSystemUtils fileSystemUtils;
    @Inject EncryptionServiceLocal encryptionService;
    @Inject ZFileManager fileManager;
    
    static final List<String> acceptedMimeType = Arrays.asList("image/jpeg",
            "image/jpg","image/png");
    
     public String getAudioFootPrint(ID3 id3){
         
         
         
        String username = FacesContext.getCurrentInstance().getExternalContext()
                .getUserPrincipal().getName();
        
        String hash ="";
        String mp3Print = username + id3.getArtist()
                +id3.getTitle()+id3.getAlbum()
                +id3.getAudioYear();
        byte[] tokenByte = mp3Print.getBytes();
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashVal = msgDigest.digest(tokenByte);
            hash = Hex.encodeHexString(hashVal);
        } catch (NoSuchAlgorithmException ex) {
            
            log.severe(ex.getMessage());
        }
        
        return hash;
    }
     
   public String getAudioFootPrint(byte[] content){
       
        String hash ="";
        try {
           
            int part = content.length/ZConstants.PART_FOR_FOOTPRINT;
            byte[] tmpPart = Arrays.copyOfRange(content, 2*part, 3*part);
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashVal = msgDigest.digest(tmpPart);
            hash = Hex.encodeHexString(hashVal);
        } catch (NoSuchAlgorithmException ex) {
            log.severe(ex.getMessage());
        }
       
        return hash;
   }
     
   public ID3 getID3Tag(byte[] fileContent,String fileName){
       
        log.info("ID3 API METADATA... ");
        Mp3File mp3file = null;
        ID3 id3 = new ID3();
        
        Path baseDir = FileSystems.getDefault().getPath(fileSystemUtils.getPathString(Folder.TMP));
        String tmpFilePrefix = "up_"; 
        String tmpFileSufix = ".mp3"; 
        Path tmpFile = null;
        
        
        try{
            tmpFile = Files.createTempFile(baseDir,tmpFilePrefix, tmpFileSufix);
        }catch(IOException e){
            
            log.severe(e.getMessage());
        }
        
        
        try {
            
            Files.write(tmpFile, fileContent);
            
           
            if(tmpFile != null){
                mp3file = new Mp3File(tmpFile.toString());
                
                //getMetaData(tmpFile.toString());
                Logger.getLogger(AudioUtils.class.getName()).info(mp3file.getFilename());
                if (mp3file.hasId3v1Tag()) {
                    
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    String tMsg = "Track: " + id3v1Tag.getTrack();
                    log.fine(tMsg);
                    
                    String track = 
                            (id3v1Tag.getTrack() != null) ? id3v1Tag.getTrack().trim():"";
                    String artist = 
                            (id3v1Tag.getArtist() != null) ? id3v1Tag.getArtist().trim():"";
                    String title = 
                            (id3v1Tag.getTitle() != null) ? id3v1Tag.getTitle().trim():"";
                    String album = 
                            (id3v1Tag.getAlbum() != null) ? id3v1Tag.getAlbum().trim():"";
                    String year = 
                            (id3v1Tag.getYear() != null) ? id3v1Tag.getYear().trim():"";
                    String genre = 
                            (id3v1Tag.getGenreDescription() != null) ? id3v1Tag.getGenreDescription().trim():"";
                    String comment = 
                            (id3v1Tag.getComment() != null) ? id3v1Tag.getComment().trim():"";
                    
                    id3.setTrack(track);
                    id3.setArtist(artist);
                    id3.setTitle(title);
                    id3.setAlbum(album);
                    id3.setAudioYear(year);
                    id3.setGenre(genre);
                    id3.setComment(comment);
                    
                   
                    
                  }
                
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();

                    byte[] albumImageData = id3v2Tag.getAlbumImage();
                    
                    if (albumImageData != null) {
                      log.info("Album image data exist ");
                      String mimeType = id3v2Tag.getAlbumImageMimeType();
                      id3.setArtWork(true);
                      id3.setArtWorkMimeType(getNormalizedMimeType(mimeType));
                      String fileHash = encryptionService.hash(albumImageData);
                      id3.setArtWorkHash(fileHash);
                      String ext = getExtFromMimeType(mimeType);
                      id3.setArtWorkExt(ext);
                      fileManager.saveArtWork(albumImageData, fileHash, ext);
                      

                    }else{
                        log.info("Album image does not data exist ");
                      //String mimeType = id3v2Tag.getAlbumImageMimeType();
                      //id3.setArtWork(true);
                      id3.setArtWorkMimeType("image/png");
                      String fileHash = encryptionService.hash(getDefaultArtWork());
                      id3.setArtWorkHash(fileHash);
                      String ext = getExtFromMimeType("image/png");
                      id3.setArtWorkExt(ext);
                      fileManager.saveDefaultArtWork(getDefaultArtWork(), fileHash, ext);
                    
                    }
              }else{
                    log.info("Album image does not data exist ");
                      //String mimeType = id3v2Tag.getAlbumImageMimeType();
                      //id3.setArtWork(true);
                      id3.setArtWorkMimeType("image/png");
                      String fileHash = encryptionService.hash(getDefaultArtWork());
                      id3.setArtWorkHash(fileHash);
                      String ext = getExtFromMimeType(".png");
                      fileManager.saveDefaultArtWork(getDefaultArtWork(), fileHash, ext);
                    
                }
            
            }
            
            
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            
            log.severe(ex.getMessage());
        }finally{
            
            if(tmpFile != null){
                try {
                    Files.delete(tmpFile);
                } catch (IOException ex) {
                   
                    log.severe(ex.getMessage());
                }
            }
        }
        
       return id3;
    }
   
    
   
  
   
   private static void handleMetadata(String key, Object value) { 
      switch (key) {
            case "album":
                log.info("MEDIA PLAYER: ".concat(value.toString()));
                break;
            case "artist": 
                log.info("MEDIA PLAYER: ".concat(value.toString()));
                break;
        }
     
}
   
   
   public static String getAudioStreamBaseLink(){
   
        String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "http://localhost:8080/zuch/zuchplayer";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "http://homefleet.cloudapp.net/Zuch/zuchplayer";
        }
       
       return path;
   }
   
   public static String getAudioSampleStreamBaseLink(){
   
        String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "http://localhost:8080/zuch/zuchsampleplayer";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "http://homefleet.cloudapp.net/Zuch/zuchplayer";  //to be changed
        }
       
       return path;
   }
   
   
  
   
   
   public InputStream getDefaultAudioSample(){
       
         Audio audio = null;
         InputStream input;
         List<String> samples = Arrays.asList("/zuch/sample/sample0.mp3",
                 "/zuch/sample/sample1.mp3","/zuch/sample/sample2.mp3",
                 "/zuch/sample/sample3.mp3","/zuch/sample/sample4.mp3",
                 "/zuch/sample/sample5.mp3","/zuch/sample/sample6.mp3",
                 "/zuch/sample/sample7.mp3","/zuch/sample/sample8.mp3",
                 "/zuch/sample/sample9.mp3");
         Collections.shuffle(samples);
         input = AudioUtils.class.getResourceAsStream(samples.get(0));
         
         return input;
   }
   
   public byte[] getDefaultArtWork(){
        byte[] res = null;
        try {
            InputStream  input = AudioUtils.class.getResourceAsStream("/zuch/images/duke.png");
            res = IOUtils.toByteArray(input);
        } catch (IOException ex) {
            Logger.getLogger(AudioUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
   }
   
   public String getExtFromMimeType(String mimeType){
       
       log.info(String.format("MP3 MIMETYPE %s", mimeType));
       
       String res = ".jpg";
       
       if(mimeType.contains("jpg")){
           res = ".jpg";
       }else if(mimeType.contains("png")){
           res = ".png";
       }else if(mimeType.contains("jpeg")){
           res = ".jpeg";
       }
       
       log.info(String.format("MP3 RETURN EXTENSION %s", res));
       return res;
   }
   
   public String getNormalizedMimeType(String mimeType){
       
       log.info(String.format("RECEIVED MP3 MIMETYPE %s", mimeType));
       
       String res = "image/png";
       
       if(mimeType.contains("jpg")){
           res = "image/jpg";
       }else if(mimeType.contains("png")){
           res = "image/png";
       }else if(mimeType.contains("jpeg")){
           res = "image/jpeg";
       }
       
       log.info(String.format("MP3 RETURN EXTENSION %s", res));
       return res;
   }
 
    
   
   
    
}
