/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
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
    
    
    //@PostConstruct
    public synchronized void buildIndex(ID3 id3){
        try {
            Directory dir = FSDirectory.open(new File(systemUtils.getSearchIndexPathString()));
            Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(dir,config);
            
           // String filePath = id3.getFootPrint() + ".txt";
        
            long start = System.currentTimeMillis();
            
            indexFile(id3);
            long end = System.currentTimeMillis();

            System.out.println("Indexing " + writer.numDocs()+ " files took "
            + (end - start) + " milliseconds");
            
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   // @Asynchronous
    /*
    public synchronized void index(String fileName){
        
        
        String filePath = fileName + ".txt";
        
        long start = System.currentTimeMillis();
        Path newFile = Paths.get(systemUtils.getSearchDataPathString(), filePath);
        File file = newFile.toFile();
        indexFile(file);
        long end = System.currentTimeMillis();
       
        System.out.println("Indexing " + writer.numDocs()+ " files took "
        + (end - start) + " milliseconds");
    }
    */
    
     private void indexFile(ID3 id3) {
        try {
            String filePath = id3.getFootPrint() + ".txt";
            Path newFile = Paths.get(systemUtils.getSearchDataPathString(), filePath);
            File file = newFile.toFile();
            System.out.println("Indexing " + file.getCanonicalPath());
            Document doc = getDocument(file,id3.getTitle());
            writer.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
    protected Document getDocument(File f,String title) {
        
        Document doc = new Document();
        
        try {
            
            doc.add(new TextField("contents", new FileReader(f)));
            doc.add(new StringField("filename", f.getName(), Field.Store.YES));
            doc.add(new StringField("title", title, Field.Store.YES));
            
                      
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return doc;
    }
    /*
     public void close(){
       try {
          
           writer.close();
       } catch (IOException ex) {
           Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
    */
     
     private  class TextFilesFilter implements FileFilter{

        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    
    }

    public IndexWriter getWriter() {
        return writer;
    }
    
     
     
}
