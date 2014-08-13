/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import com.sun.xml.ws.developer.Stateful;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.SystemUtils;
import zuch.model.Audio;

/**
 *
 * @author florent
 */
@Stateless
public class ZFileSystemUtils {
    
     static final Logger log = Logger.getLogger("zuch.service.ZFileSystemUtils");
    
    public  String getTempMp3UrlString(){
       String path = "";
       
      
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path = "c:/zuch/tmp/";
            
               
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/tmp/";
       
        }
       
       return path;
  }
    
  public String getSearchDataPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/search_data";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/search_data";
        }
       
       return path;
  }
  
  public String getEnSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/search_index/";
        }
       
       return path;
  }
  
   public String getEnSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/spellchecker_data/";
        }
       
       return path;
  }
  
  public String getFrSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/fr_search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/fr_search_index/";
        }
       
       return path;
  }
  
  public String getFrSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/fr_spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/fr_spellchecker_data/";
        }
       
       return path;
  }
  
  public String getSpSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/sp_search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/sp_search_index/";
        }
       
       return path;
  }
  
  public String getSpSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/sp_spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/sp_spellchecker_data/";
        }
       
       return path;
  }
  
   public String getAlbumImagePath(Audio audio){
        String path = "";
        
      if(audio.getId3().hasArtWork()){
         if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "C:\\zuch\\images\\"+audio.getId3().getFootPrint()+".jpg";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/images/"+audio.getId3().getFootPrint()+".jpg";
        }
      }else{
      
          path = "C:\\zuch\\images\\music.png";
      }
        
       
       
       return path;
   }
    
  public String normalizeFileName(String fileName){
  
      Path path = Paths.get(fileName);
      return path.getFileName().toString();
  }
    
}
