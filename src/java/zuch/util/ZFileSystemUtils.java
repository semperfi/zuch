/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.SystemUtils;
import zuch.model.Audio;
import zuch.model.ZConstants;

/**
 *
 * @author florent
 */
//@RequestScoped
@Stateless
public class ZFileSystemUtils {
    
     static final Logger log = Logger.getLogger(ZFileSystemUtils.class.getName());
    
    /*
    public  String getTempMp3UrlString(){
       String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path = "c:/zuch/tmp/";
            
               
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/tmp/";
       
        }
       
       return path;
  }
    
  public String getSearchDataPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/search_data";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/search_data";
        }
       
       return path;
  }
  
  public String getEnSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/search_index/";
        }
       
       return path;
  }
  
   public String getEnSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/spellchecker_data/";
        }
       
       return path;
  }
  
  public String getFrSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/fr_search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/fr_search_index/";
        }
       
       return path;
  }
  
  public String getFrSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/fr_spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/fr_spellchecker_data/";
        }
       
       return path;
  }
  
  public String getSpSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/sp_search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/sp_search_index/";
        }
       
       return path;
  }
  
  public String getSpSpellCheckerPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/sp_spellchecker_data/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/sp_spellchecker_data/";
        }
       
       return path;
  }
  
   public String getArtWorkPathString(){
        String path = "";
           
        if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "C:/zuch/images/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/images/";
        }
     
       
       return path;
   }
   
  
   
  public String getTracksPathString(){
       String path = "";
        
       
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/tracks/";
            
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/tracks/";
             
        }
       
       return path;
  } 
  
  
  
  public String getSamplesPathString(){
       String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/samples/";
           
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/usr/zuch/samples/";
            
        }
       
       return path;
  }
  
     */
  
  public String getPathString(Folder folder){
      String path = "";
      
      
          switch(folder){
              case EN_INDEX:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_EN_INDEX_PATH : ZConstants.NIX_EN_INDEX_PATH;
                  break;
              case FR_INDEX:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_FR_INDEX_PATH : ZConstants.NIX_FR_INDEX_PATH;
                  break;
              case SP_INDEX:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_SP_INDEX_PATH : ZConstants.NIX_SP_INDEX_PATH ;
                  break;
              case EN_SPELLCHK:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_EN_SPELLCHK_PATH :  ZConstants.NIX_EN_SPELLCHK_PATH;
                  break;
              case FR_SPELLCHK:
                  path = SystemUtils.IS_OS_WINDOWS ?
                          ZConstants.WIN_FR_SPELLCHK_PATH : ZConstants.NIX_FR_SPELLCHK_PATH;
                  break;
              case SP_SPELLCHK:
                  path = SystemUtils.IS_OS_WINDOWS ?
                          ZConstants.WIN_SP_SPELLCHK_PATH : ZConstants.NIX_SP_SPELLCHK_PATH;
                  break;
              case TMP:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_TMP_PATH : ZConstants.NIX_TMP_PATH;
                  break;
              case IMAGE:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_IMAGE_PATH : ZConstants.NIX_IMAGE_PATH;
                  break;
              case TRACK:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_TRACK_PATH : ZConstants.NIX_TRACK_PATH;
                  break;
              case SAMPLE:
                  path = SystemUtils.IS_OS_WINDOWS ? 
                          ZConstants.WIN_SAMPLE_PATH : ZConstants.NIX_SAMPLE_PATH;
                  break;
          }
          
      
      return path;
  
  }
  
  
  public String normalizeFileName(String fileName){
  
      Path path = Paths.get(fileName);
      return path.getFileName().toString();
  }
    
}
