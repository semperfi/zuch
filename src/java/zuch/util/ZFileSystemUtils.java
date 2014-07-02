/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ejb.Stateless;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class ZFileSystemUtils {
    
    public  String getTempMp3UrlString(){
       String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/tmp/";
            
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
  
  public String getSearchIndexPathString(){
      String path = "";
        
       if(SystemUtils.IS_OS_WINDOWS){
            
            path =  "c:/zuch/search_index/";
            
        }else if(SystemUtils.IS_OS_UNIX){
            
            path = "/root/zuch/search_index/";
        }
       
       return path;
  }
    
  public String normalizeFileName(String fileName){
  
      Path path = Paths.get(fileName);
      return path.getFileName().toString();
  }
    
}
