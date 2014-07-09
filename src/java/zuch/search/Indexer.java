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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
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
   private IndexWriter writer;
  // private IndexWriter frWriter;
   
   private static  final List<String> words = Arrays.asList("a","à");
   
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
    
    
    @Lock(LockType.WRITE)
    public void buildEnIndex(Audio audio,ID3 id3){
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getSearchIndexPathString()));
            Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(writer,audio,id3);
            long end = System.currentTimeMillis();

            System.out.println("Indexing " + writer.numDocs()+ " files took "
            + (end - start) + " milliseconds");
            
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Lock(LockType.WRITE)
    public void buildFrIndex(Audio audio,ID3 id3){
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getFrSearchIndexPathString()));
            Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(writer,audio,id3);
            long end = System.currentTimeMillis();

            System.out.println("Indexing " + writer.numDocs()+ " files took "
            + (end - start) + " milliseconds");
            
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   // @Asynchronous
   
     private void indexFile(IndexWriter inWriter,Audio audio,ID3 id3) {
        try {
           
            System.out.println("Indexing " + 
                    id3.getArtist() + ":"+ id3.getTitle());
            //Document doc = getDocument(file,audio,id3);
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
                  
            
            doc.add(new TextField("contents",contents, Field.Store.YES));
            doc.add(new TextField("title",id3.getTitle(), Field.Store.YES));
            doc.add(new TextField("artist",id3.getArtist(), Field.Store.YES));
            doc.add(new TextField("album",id3.getAlbum(), Field.Store.YES));
            doc.add(new StringField("year",id3.getAudioYear(), Field.Store.YES));
            doc.add(new StringField("genre",id3.getGenre(), Field.Store.YES));
            doc.add(new StringField("footprint",id3.getFootPrint(), Field.Store.YES));
            doc.add(new LongField("id", audio.getId(), Field.Store.YES));
       
        return doc;
    }
   
     private  class TextFilesFilter implements FileFilter{

        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    
    }
     
    @Lock(LockType.WRITE)
    public void deleteDocument(ID3 id3){
        
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getSearchIndexPathString()));
           Analyzer analyser = new StandardAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
           writer = new IndexWriter(dir,config);
          
           
           writer.deleteDocuments(new Term("footprint", id3.getFootPrint()));
           writer.close();
           
       } catch (IOException ex) {
           Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
       }
        
    }

    public IndexWriter getWriter() {
        return writer;
    }
    
     
     
}
