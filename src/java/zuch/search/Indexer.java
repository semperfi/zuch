/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
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
import zuch.qualifier.Added;
import zuch.qualifier.AudioClear;
import zuch.qualifier.AudioRebuilt;
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
    
   static final Logger log = Logger.getLogger("zuch.service.Indexer");
   
   @Inject ZFileSystemUtils systemUtils;
   @Inject ZSpellChecker spellChecker;
  // private IndexWriter writer;
  // private IndexWriter frWriter;
   
   private static  final List<String> words = Arrays.asList("a","à");
   
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
   @Inject
   private Event<Date> startIndexingEvent;
   
   @Inject
   private @Added Event<Audio>  addAudioEvent; //use to rebuild index
   
   
   /*
    * delete current document from from index 
    */
   @Asynchronous
   public void deleteAndRebuild(@Observes @AudioRebuilt Audio audio){
       
      // ID3 currentId3 = audio.getId3();
      // log.info(String.format("--> Current ID3 ID: %d", currentId3.getId()));
       deleteEnDocument(audio);
       deleteFrDocument(audio);
       deleteSpDocument(audio);
       
       addAudioEvent.fire(audio);
   
   }
   
   @Asynchronous
   public void clearSearchIndex(@Observes @AudioClear Audio audio){
     //  ID3 currentId3 = audio.getId3();
       deleteEnDocument(audio);
       deleteFrDocument(audio);
       deleteSpDocument(audio);
   }
   
    @Asynchronous
   // @Lock(LockType.WRITE)
    public void buildEnIndex(@Observes @Added Audio audio){
        
       log.warning("ADD AUDIO OBSERVER RECEIVED EVENT...");
        
       log.info(String.format("METHOD buildEnIndex(Audio audio,ID3 id3) ON THREAD [%s]", 
                Thread.currentThread().getName()));  
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.EN_INDEX)));
            Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
           // ID3 id3 = audio.getId3();
            
            indexFile(writer,audio);
            long end = System.currentTimeMillis();

            log.info(String.format("EN Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
            
            writer.close();
            
            //build spell checker
            spellChecker.buildEnSpellChecker();
            
           // startIndexingEvent.fire(new Date());
            
            
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
    
    
    
    
    @Asynchronous
    //@Lock(LockType.WRITE)
    public void buildFrIndex(@Observes @Added Audio audio){
        
        log.info(String.format("METHOD buildFrIndex(Audio audio,ID3 id3) ON THREAD [%s]", 
                Thread.currentThread().getName()));  
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.FR_INDEX)));
            Analyzer analyser = new ZuchFrenchAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            ID3 id3 = audio.getId3();
            indexFile(writer,audio);
            long end = System.currentTimeMillis();

            log.info(String.format("FR Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
            
            
            writer.close();
            
            //build spell checker
            spellChecker.buildFrSpellChecker();
            
            
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
    
   
    @Asynchronous
   // @Lock(LockType.WRITE)
    public void buildSpIndex(@Observes @Added Audio audio){
        
       log.info(String.format("METHOD buildSpIndex(Audio audio,ID3 id3) ON THREAD [%s]", 
                Thread.currentThread().getName()));  
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.SP_INDEX)));
            Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
           
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            ID3 id3 = audio.getId3();
            indexFile(writer,audio);
            
            long end = System.currentTimeMillis();

            log.info(String.format("SP Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
           
            writer.close();
            
             
            //build spell checker
            spellChecker.buildSpSpellChecker();
            
            
        } catch (IOException ex) {
            if(writer != null ){
                try {
                    writer.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public void deleteEnDocument(Audio audio){
        
        log.info("DELETING FROM ENGLISH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.EN_INDEX)));
           Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
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
    
   // @Lock(LockType.WRITE)
    public void deleteFrDocument(Audio audio){
        
        log.info("DELETING FROM FRENCH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.FR_INDEX)));
           Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
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
    
    //@Lock(LockType.WRITE)
    public void deleteSpDocument(Audio audio){
        
        log.info("DELETING FROM SPANISH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.SP_INDEX)));
           Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
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
    
    
    
    public void checkIndexing(@Observes Date event){
        log.warning("IT'S TIME TO INDEX: ".concat(event.toString()));
        
    }

  
     
}
