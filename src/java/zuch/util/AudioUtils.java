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
import java.io.ByteArrayInputStream;
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
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import zuch.model.Audio;
import zuch.model.AudioContent;
import zuch.model.ID3;

/**
 *
 * @author florent
 */
@Stateless
public class AudioUtils {
    
    static final Logger log = Logger.getLogger("zuch.service.AudioUtils");
    @Inject ZFileSystemUtils fileSystemUtils;
    
    
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
     
   public ID3 getID3Tag(byte[] fileContent,String fileName){
       
        log.info("ID3 API METADATA... ");
        Mp3File mp3file = null;
        ID3 id3 = new ID3();
        
        Path baseDir = FileSystems.getDefault().getPath(fileSystemUtils.getTempMp3UrlString());
        String tmpFilePrefix = "up_"; 
        String tmpFileSufix = ".mp3"; 
        Path tmpFile = null;
        
        
        try{
            tmpFile = Files.createTempFile(baseDir,tmpFilePrefix, tmpFileSufix);
        }catch(IOException e){
            
            log.severe(e.getMessage());
        }
        
        
        try {
            
            
            ByteArrayInputStream bin = new ByteArrayInputStream(fileContent);
            
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
                  log.fine("Album image data exist ");
                  
                }
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
   
    
   /*
   public  void getMetaData(String fileName){
       
       log.info("MEDIA PLAYER METADATA... ");
        
            Path path = Paths.get(fileName);
            String chemin = path.toUri().toString();
            log.info(chemin);
            Media media = new Media(chemin);
           // Logger.getLogger(AudioUtils.class.getName()).info(mp3file.getFilename());
            media.getMetadata().addListener(new MapChangeListener<String, Object>() { 
                @Override 
                public void onChanged(Change<? extends String, ? extends Object> ch) { 
                    if (ch.wasAdded()) { 
                        handleMetadata(ch.getKey(), ch.getValueAdded()); 
                    } 
                } 
            });
        
      
    }
   */
   
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
            
            path =  "http://localhost:8080/Zuch/zuchplayer";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "http://192.162.71.141:8080/Zuch/zuchplayer";
        }
       
       return path;
   }
   
   public static String getAudioSampleStreamBaseLink(){
   
        String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "http://localhost:8080/Zuch/zuchsampleplayer";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "http://192.162.71.141:8080/Zuch/zuchplayer";
        }
       
       return path;
   }
   
   
   public Audio getDefaultAudioSample(){
       
         Audio audio = null;
         try {
             
             List<String> samples = Arrays.asList("/zuch/sample/sample0.mp3",
                     "/zuch/sample/sample1.mp3","/zuch/sample/sample2.mp3",
                     "/zuch/sample/sample3.mp3","/zuch/sample/sample4.mp3",
                     "/zuch/sample/sample5.mp3","/zuch/sample/sample6.mp3",
                     "/zuch/sample/sample7.mp3","/zuch/sample/sample8.mp3",
                     "/zuch/sample/sample9.mp3");
             Collections.shuffle(samples);
             
             InputStream input = AudioUtils.class.getResourceAsStream(samples.get(0));
             
             
             byte[] content = IOUtils.toByteArray(input);
             ID3 id3 = getID3Tag(content, "sample.mp3");
             String footPrint = samples.get(0);
             id3.setFootPrint(footPrint);
             AudioContent audioContent = new AudioContent();
             audioContent.setContent(content);
             audio = new Audio();
             audio.setId3(id3);
             audio.setContent(audioContent);
             
             
             return audio;
         } catch (IOException ex) {
             Logger.getLogger(AudioUtils.class.getName()).log(Level.SEVERE, null, ex);
             log.severe(ex.getMessage());
         }
         
         return audio;
   }
    
}
