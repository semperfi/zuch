/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import zuch.model.Audio;
import zuch.model.ID3;
import zuch.qualifier.AudioAdded;
import zuch.event.EventService;
import zuch.qualifier.AudioDeleted;
import zuch.util.Folder;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value=30,unit=TimeUnit.MINUTES)
public class Indexer {
    
   static final Logger log = Logger.getLogger(Indexer.class.getName());
   
   @Inject ZFileSystemUtils systemUtils;
   @Inject ZSpellChecker spellChecker;
   
   @Inject EventService eventService;
   
   private static  final List<String> words = Arrays.asList("a","à");
   
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
  
   
   
   @Asynchronous
   public void OnAudioAdded(@Observes @AudioAdded Audio audio){
       buildEnIndex(audio);
       buildFrIndex(audio);
      
       
       /*
       * used by zuch.search.ZSpellChecker 
       */
       eventService.getAudioIndexedEvent().fire("indexed");
   }
   
   @Asynchronous
   public void OnAudioDeleted(@Observes @AudioDeleted Audio audio){
       deleteEnDocument(audio);
       deleteFrDocument(audio);
       
       /*
       * used by zuch.search.ZSpellChecker 
       */
       eventService.getAudioRemovedFromIndexEvent().fire("removedFromIndex");
   }
   
     
    private void buildEnIndex(Audio audio){
        
       log.warning("ADD AUDIO OBSERVER RECEIVED EVENT...");
        
       log.info(String.format("METHOD buildEnIndex(Audio audio,ID3 id3) ON THREAD [%s]", 
                Thread.currentThread().getName()));  
       
       Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
       buildIndex(audio, Folder.EN_INDEX, analyser);
       
            
        
    }
    
   
    
    private void buildFrIndex(Audio audio){
        
        log.info(String.format("METHOD buildFrIndex(Audio audio,ID3 id3) ON THREAD [%s]", 
                Thread.currentThread().getName()));  
        
        Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
        buildIndex(audio, Folder.FR_INDEX, analyser);
            
      
        
    }
    
    
    
   @Asynchronous
    private void indexFile(IndexWriter inWriter,Audio audio) {
        try {
           
            ID3 id3 = audio.getId3();
            log.info(String.format("Indexing %s : %s",id3.getArtist(),id3.getTitle() ));
            Document doc = getDocument(audio);
            inWriter.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
    private Document getDocument(Audio audio) {
        
        Document doc = new Document();
        ID3 id3 = audio.getId3();
               
            String contents = new StringBuilder()
                    .append(id3.getTitle())
                    .append("\n")
                    .append(id3.getArtist())
                    .append("\n")
                    .append(id3.getAlbum())
                    .append("\n")
                    .append(id3.getAudioYear())
                    .append("\n")
                    .append(id3.getGenre())
                    .append("\n")
                    .append(id3.getArtist())
                    .append("\n")
                    .append(audio.getAvgRating())
                    .toString();
                  
            
            doc.add(new TextField("contents",getFieldValue(contents), Field.Store.YES));
            doc.add(new TextField("title",getFieldValue(id3.getTitle()), Field.Store.YES));
            doc.add(new TextField("artist",getFieldValue(id3.getArtist()), Field.Store.YES));
            doc.add(new TextField("album",getFieldValue(id3.getAlbum()), Field.Store.YES));
            doc.add(new StringField("year",getFieldValue(id3.getAudioYear()), Field.Store.YES));
            doc.add(new StringField("genre",getFieldValue(id3.getGenre()), Field.Store.YES));
            doc.add(new StringField("footprint",getFieldValue(audio.getFootPrint()), Field.Store.YES));
            doc.add(new IntField("avgRating", audio.getAvgRating(), Field.Store.YES));
            doc.add(new LongField("id", audio.getId(), Field.Store.YES));
       
        return doc;
    }
   
    private String getFieldValue(String value){
        
        return value == null  ? "":value;
    }
    
   
   
    //@Lock(LockType.WRITE)
  private void deleteEnDocument(Audio audio){
        
        log.info("DELETING FROM ENGLISH INDEX...");
        
        Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
        deleteDocument(audio, Folder.EN_INDEX, analyser);
    }
    
   // @Lock(LockType.WRITE)
   private void deleteFrDocument(Audio audio){
        
        log.info("DELETING FROM FRENCH INDEX...");
        Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
        deleteDocument(audio, Folder.FR_INDEX, analyser);
        
    }
    
    
    
    
    private IndexWriter getIndexWriter(Folder folder, Analyzer analyser){
       
       IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(folder)));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
        }catch(IOException ex){
            
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return writer;
   }
   
    private void buildIndex(Audio audio,Folder folder, Analyzer analyser){
       
      IndexWriter writer = null;
        
        try {
            
            writer = getIndexWriter(folder, analyser);
            long start = System.currentTimeMillis();
            
             
            indexFile(writer,audio);
            long end = System.currentTimeMillis();

            log.info(String.format("%s Indexing %s files took %d milliseconds",folder.name(),
                    writer.numDocs(),(end - start) ));
            
            writer.close();
            
                        
        }catch(IOException ex){
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   private void deleteDocument(Audio audio,Folder folder, Analyzer analyser){
        
       IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(folder)));
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
           writer = new IndexWriter(dir,config);
          
           
           writer.deleteDocuments(new Term("footprint", audio.getFootPrint()));
           
           writer.close();
           
       } catch (IOException ex) {
           if(writer != null){
               try {
                   writer.close();
               } catch (IOException ex1) {
                   Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex1);
               }
           }
           Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
       }
        
   }
    
   
     
}
