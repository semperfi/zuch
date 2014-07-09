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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;

import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import org.apache.lucene.store.Directory;

import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class Searcher {
    
    static final Logger log = Logger.getLogger("zuch.service.Searcher");
    
    @Inject ZFileSystemUtils systemUtils;
    
   private static  final List<String> words = Arrays.asList("a","à");
   
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);

    public void searchEn(String q){
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getSearchIndexPathString()));
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                
                QueryBuilder stdBuilder = new QueryBuilder(new EnglishAnalyzer(Version.LUCENE_4_9, stopWords));
                 
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(stdBuilder.createBooleanQuery("contents", q), BooleanClause.Occur.MUST);
                
                
                long start = System.currentTimeMillis();
                TopDocs hits = indexSearcher.search(booleanQuery, 15);
                long end = System.currentTimeMillis();
                
                 System.out.println("_____English index search_________");
                String msg = "Found " + hits.totalHits +
                        " hits(s) (in " + (end - start) +
                        " milliseconds) that matched query '" +
                        q + "':";
                
               log.warning(msg);
                
                for(ScoreDoc scoreDoc : hits.scoreDocs){
                    Document doc  = indexSearcher.doc(scoreDoc.doc);
                    
               
                   
                    System.out.println(doc.get("artist"));
                    System.out.println(doc.get("title"));
                    System.out.println("----------------------------------");
                  
                }
            }  
            
        }   catch (Exception ex) {
                if(ex instanceof NullPointerException){
                     log.warning("Cannot search for stop world");
                }else{
                    log.severe(ex.getMessage());
                }
           
        }
    
    }
    
    
   public void searchFr(String q){
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getFrSearchIndexPathString()));
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                
                QueryBuilder stdBuilder = new QueryBuilder(new FrenchAnalyzer(Version.LUCENE_4_9, stopWords));
                 
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(stdBuilder.createBooleanQuery("contents", q), BooleanClause.Occur.MUST);
                
                
                long start = System.currentTimeMillis();
                TopDocs hits = indexSearcher.search(booleanQuery, 15);
                long end = System.currentTimeMillis();
                
                 
                System.out.println("_____French index search_________");
                
                String msg = "Found " + hits.totalHits +
                        " hits(s) (in " + (end - start) +
                        " milliseconds) that matched query '" +
                        q + "':";
                
               log.warning(msg);
                
                for(ScoreDoc scoreDoc : hits.scoreDocs){
                    Document doc  = indexSearcher.doc(scoreDoc.doc);
                    
               
                    
                    System.out.println(doc.get("artist"));
                    System.out.println(doc.get("title"));
                    System.out.println("----------------------------------");
                  
                }
            }  
            
        }   catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
     
    
    
}
