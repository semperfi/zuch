/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import zuch.model.ID3;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class Content {
    static final Logger log = Logger.getLogger("zuch.service.Content");
    
    @Inject ZFileSystemUtils systemUtils;
    @Inject Indexer indexer;
    
   // @Asynchronous
    public void buildContent(ID3 id3){
        try {
            
            String contentFile = id3.getFootPrint() + ".txt";
            
            Path newFile = 
                    Paths.get(systemUtils.getSearchDataPathString(),contentFile);
            Files.createFile(newFile);
            
            List<String> lines = new ArrayList<>();
            
            lines.add(id3.getAlbum());
            lines.add(id3.getArtist());
            lines.add(id3.getAudioYear());
            lines.add(id3.getComment());
            lines.add(id3.getGenre());
            lines.add(id3.getTitle());
            //lines.add(id3.getTrack());
            //lines.add(id3.getVersion().toString());
            
            Files.write(newFile, lines, StandardOpenOption.APPEND);
            
            
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }
}
