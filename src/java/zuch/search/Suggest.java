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
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class Suggest {
    
   static final Logger log = Logger.getLogger("zuch.service.ZSpellCheker");
   @Inject ZFileSystemUtils systemUtils;
   
   String[] suggestions;

   public List<String> buildSuggestions(String wordToRespell){
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSpellCheckerPathString()));
           SpellChecker spell = new SpellChecker(dir);
           spell.setStringDistance(new LevensteinDistance());
           suggestions = spell.suggestSimilar(wordToRespell, 2);
           System.out.println(suggestions.length +  " suggestions for '" +  wordToRespell + "':");
           for(String suggestion : suggestions){
                System.out.println(" " + suggestion);
            }
           
       } catch (IOException ex) {
           Logger.getLogger(Suggest.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return Arrays.asList(suggestions);
   }
}
