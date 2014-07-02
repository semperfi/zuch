/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

    public void search(String q){
        try {
            Directory dir = FSDirectory.open(new File(systemUtils.getSearchIndexPathString()));
            
            try (IndexReader indexReader = DirectoryReader.open(dir)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                
                QueryBuilder builder = new QueryBuilder(new EnglishAnalyzer(Version.LUCENE_4_9));
                Query query = builder.createBooleanQuery("contents", q);
                
                long start = System.currentTimeMillis();
                TopDocs hits = indexSearcher.search(query, 15);
                long end = System.currentTimeMillis();
                
                String msg = "Found " + hits.totalHits +
                        " hits(s) (in " + (end - start) +
                        " milliseconds) that matched query '" +
                        q + "':";
                
               log.warning(msg);
                
                for(ScoreDoc scoreDoc : hits.scoreDocs){
                    Document doc  = indexSearcher.doc(scoreDoc.doc);
                    System.out.println(doc.get("filename"));
                    System.out.println(doc.get("title"));
                }
            }
            
        }   catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}
