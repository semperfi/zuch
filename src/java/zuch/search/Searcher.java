/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
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
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.SearchResult;
import zuch.service.AudioManagerLocal;
import zuch.util.Folder;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@RequestScoped
public class Searcher {
    
    static final Logger log = Logger.getLogger(Searcher.class.getName());
    
    @Inject ZFileSystemUtils systemUtils;
    @Inject Suggest suggest;
    @Inject AudioManagerLocal audioManager;
    
    private static  final List<String> words = Arrays.asList("a","à");
   
    private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);

    private List<Document> searchEn(String q){
        
        log.info("------ENGLISH------");
        Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
        return search(Folder.EN_INDEX, analyser, q);
       
    }
    
    
    private List<Document> searchFr(String q){
        
        log.info("------FRENCH------");
        Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
        return  search(Folder.FR_INDEX, analyser, q);
        
    }
   
   
    private List<Document> search(Folder folder,Analyzer analyser,String q){
       
        List<Document> searchResult = new ArrayList<>();
         
        try {
            Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(folder)));
           
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher  = new IndexSearcher(indexReader);
                
                QueryBuilder stdBuilder = new QueryBuilder(analyser);
                 
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(stdBuilder.createBooleanQuery("contents", q), BooleanClause.Occur.MUST);
                             
                long start = System.currentTimeMillis();
                TopDocs hits = indexSearcher.search(booleanQuery, 15);
                long end = System.currentTimeMillis();
                
                log.info("_____Index search_________");
                String msg = "Found " + hits.totalHits +
                        " hits(s) (in " + (end - start) +
                        " milliseconds) that matched query '" +
                        q + "':";
                
               log.warning(msg);
                           
               for(ScoreDoc scoreDoc : hits.scoreDocs){
                    Document doc  = indexSearcher.doc(scoreDoc.doc);
                    searchResult.add(doc);
                    log.info(doc.get("artist"));
                    log.info(doc.get("title"));
                    log.info("----------------------------------");
                  
                }
            }  
            
           // suggest.buildSuggestions(q);
            
        }   catch (Exception ex) {
                if(ex instanceof NullPointerException){
                     log.warning("Cannot search for stop world");
                }else{
                   
                    log.severe(ex.getMessage());
                }
       }
        return searchResult;
   }
   
   
    public List<SearchResult> luceneSearchForAudio(String searchToken){
        
        long start = System.currentTimeMillis();
        
        List<SearchResult> resultList = new ArrayList<>();
       // Map<String,Integer> docs = new HashMap<>();
        List<List<Document>> docList = new ArrayList<>();
        
        List<Document> englishDoc = searchEn(searchToken);
        List<Document> frenchDoc = searchFr(searchToken);
        
       
        docList.add(frenchDoc);
        docList.add(englishDoc);
        
        List<Document> selectedList = max(docList); //get list with max size cause it's likely has best hits
        selectedList.stream().forEach((doc) -> {
            resultList.add(toSearchResult(doc));
        });
        
          
       long end = System.currentTimeMillis();

      log.info(String.format("Search %s files took %d milliseconds",
                    resultList.size(),(end - start) ));
      
      return resultList;
    }
    
   
    
    private List<Document> max(List<List<Document>> docs){
        
        List<Document> maximum = docs.get(0);
        for(List<Document> lDoc : docs){
            
            if(lDoc.size() > maximum.size() ){
                maximum = lDoc;
            }
        }
        
        
        return maximum;
    }
    
    public SearchResult toSearchResult(Document doc){
        
        SearchResult res = new SearchResult();
        res.setAudioId(Long.parseLong(doc.get("id")) );
        res.setOwner(getOwnerId(Long.parseLong(doc.get("id"))));
        res.setTitle(doc.get("title"));
        res.setArtist(doc.get("artist"));
        res.setAlbum(doc.get("album"));
        res.setAudioYear(doc.get("year"));
        res.setGenre(doc.get("genre"));
        res.setContents(doc.get("contents"));
        
        if(doc.get("avgRating") != null){//avgRating has been add later and some value are still null
            res.setAvgRating(Integer.valueOf(doc.get("avgRating")));
        }else{
            res.setAvgRating(0);
        }
        
        res.setFootPrint(doc.get("footprint"));
        
        return res;
        
    }
    
     private String getOwnerId(long audioId){
        
        String owner = "";
        
        try {
            Audio audio = audioManager.getAudio(audioId);
            owner = audio.getOwner().getId();
        } catch (AudioNotFound ex) {
            log.severe(ex.getMessage());
        }
        
        return owner; 
    }

    
}
