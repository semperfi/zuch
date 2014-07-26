/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Singleton
public class Indexer {
    
   static final Logger log = Logger.getLogger("zuch.service.Indexer");
   
   @Inject ZFileSystemUtils systemUtils;
  // private IndexWriter writer;
  // private IndexWriter frWriter;
   
   private static  final List<String> words = Arrays.asList("a","à");
   
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
   @Inject
   private Event<Date> startIndexingEvent;
   
  
    
    
    @Lock(LockType.WRITE)
    public void buildEnIndex(Audio audio,ID3 id3){
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSearchIndexPathString()));
            Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(writer,audio,id3);
            long end = System.currentTimeMillis();

            log.info(String.format("EN Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
            
            writer.close();
            
            startIndexingEvent.fire(new Date());
            
            
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
    
    @Lock(LockType.WRITE)
    public void buildFrIndex(Audio audio,ID3 id3){
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getFrSearchIndexPathString()));
            Analyzer analyser = new ZuchFrenchAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(writer,audio,id3);
            long end = System.currentTimeMillis();

            log.info(String.format("FR Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
            
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
    
    @Lock(LockType.WRITE)
    public void buildSpIndex(Audio audio,ID3 id3){
        
        IndexWriter writer = null;
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getSpSearchIndexPathString()));
            Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
           
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(writer,audio,id3);
            long end = System.currentTimeMillis();

            log.info(String.format("SP Indexing %s files took %d milliseconds",
                    writer.numDocs(),(end - start) ));
            
            writer.close();
            
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
    
    
   // @Asynchronous
   
     private void indexFile(IndexWriter inWriter,Audio audio,ID3 id3) {
        try {
           
            
            log.info(String.format("Indexing %s : %s",id3.getArtist(),id3.getTitle() ));
            Document doc = getDocument(audio,id3);
            inWriter.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
    private Document getDocument(Audio audio,ID3 id3) {
        
        Document doc = new Document();
        
               
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
                    .toString();
                  
            
            doc.add(new TextField("contents",getFieldVAlue(contents), Field.Store.YES));
            doc.add(new TextField("title",getFieldVAlue(id3.getTitle()), Field.Store.YES));
            doc.add(new TextField("artist",getFieldVAlue(id3.getArtist()), Field.Store.YES));
            doc.add(new TextField("album",getFieldVAlue(id3.getAlbum()), Field.Store.YES));
            doc.add(new StringField("year",getFieldVAlue(id3.getAudioYear()), Field.Store.YES));
            doc.add(new StringField("genre",getFieldVAlue(id3.getGenre()), Field.Store.YES));
            doc.add(new StringField("footprint",getFieldVAlue(id3.getFootPrint()), Field.Store.YES));
            doc.add(new LongField("id", audio.getId(), Field.Store.YES));
       
        return doc;
    }
   
    private String getFieldVAlue(String value){
        
        return value == null  ? "":value;
    }
    
     private  class TextFilesFilter implements FileFilter{

        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    
    }
     
    @Lock(LockType.WRITE)
    public void deleteEnDocument(ID3 id3){
        
        log.info("DELETING FROM ENGLISH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSearchIndexPathString()));
           Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
           writer = new IndexWriter(dir,config);
          
           
           writer.deleteDocuments(new Term("footprint", id3.getFootPrint()));
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
    
    @Lock(LockType.WRITE)
    public void deleteFrDocument(ID3 id3){
        
        log.info("DELETING FROM FRENCH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getFrSearchIndexPathString()));
           Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
           writer = new IndexWriter(dir,config);
          
           
           writer.deleteDocuments(new Term("footprint", id3.getFootPrint()));
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
    
    @Lock(LockType.WRITE)
    public void deleteSpDocument(ID3 id3){
        
        log.info("DELETING FROM SPANISH INDEX...");
        IndexWriter writer = null;
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getSpSearchIndexPathString()));
           Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
           writer = new IndexWriter(dir,config);
          
           
           writer.deleteDocuments(new Term("footprint", id3.getFootPrint()));
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
