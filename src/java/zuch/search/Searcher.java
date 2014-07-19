/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
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


import org.apache.lucene.store.Directory;

import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;
import zuch.model.Audio;
import zuch.model.SearchResult;
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

    public List<Document> searchEn(String q){
        
        List<Document> result = new ArrayList<>();
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSearchIndexPathString()));
            
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
                    result.add(doc);
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
        return result;
    }
    
    
   public List<Document> searchFr(String q){
       
       List<Document> result = new ArrayList<>();
       
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getFrSearchIndexPathString()));
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                
                QueryBuilder stdBuilder = new QueryBuilder(new ZuchFrenchAnalyzer());
                 
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
                    result.add(doc);
                    System.out.println(doc.get("artist"));
                    System.out.println(doc.get("title"));
                    System.out.println("----------------------------------");
                  
                }
            }  
            
        }   catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
   
    public List<Document> searchSp(String q){
        
        List<Document> result = new ArrayList<>();
        
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getSpSearchIndexPathString()));
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                
                QueryBuilder stdBuilder = new QueryBuilder(new SpanishAnalyzer(Version.LUCENE_4_9, stopWords));
                 
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(stdBuilder.createBooleanQuery("contents", q), BooleanClause.Occur.MUST);
                
                
                long start = System.currentTimeMillis();
                TopDocs hits = indexSearcher.search(booleanQuery, 15);
                long end = System.currentTimeMillis();
                
                 
                System.out.println("_____Spanish index search_________");
                
                String msg = "Found " + hits.totalHits +
                        " hits(s) (in " + (end - start) +
                        " milliseconds) that matched query '" +
                        q + "':";
                
               log.warning(msg);
                
                for(ScoreDoc scoreDoc : hits.scoreDocs){
                    Document doc  = indexSearcher.doc(scoreDoc.doc);
                    result.add(doc);
                    System.out.println(doc.get("artist"));
                    System.out.println(doc.get("title"));
                    System.out.println("----------------------------------");
                  
                }
            }  
            
        }   catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
     
    
    public List<SearchResult> luceneSearchForAudio(String searchToken){
        List<SearchResult> resultList = new ArrayList<>();
       // Map<String,Integer> docs = new HashMap<>();
        List<List<Document>> docList = new ArrayList<>();
        
        List<Document> englishDoc = searchEn(searchToken);
        List<Document> frenchDoc = searchFr(searchToken);
        List<Document> spanishDoc = searchSp(searchToken);
        
       
        docList.add(frenchDoc);
        docList.add(englishDoc);
        docList.add(spanishDoc);
        
        List<Document> selectedList = max(docList); //get list with max size cause it likely has best hits
        
        for(Document doc : selectedList){
            resultList.add(toSearchResult(doc));
        }
        
       
        
        return resultList;
    }
    
    private List<Document> max(List<List<Document>> docs){
        
        List<Document> max = docs.get(0);
        for(List<Document> lDoc : docs){
            
            if(lDoc.size() > max.size() ){
                max = lDoc;
            }
        }
        
        return max;
    }
    
    public SearchResult toSearchResult(Document doc){
        
        SearchResult res = new SearchResult();
        res.setAudioId(Long.parseLong(doc.get("id")) );
        res.setTitle(doc.get("title"));
        res.setArtist(doc.get("artist"));
        res.setAlbum(doc.get("album"));
        res.setAudioYear(doc.get("year"));
        res.setGenre(doc.get("genre"));
        res.setContents(doc.get("contents"));
        res.setFootPrint(doc.get("footprint"));
        
        return res;
        
    }

    
}
