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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.search.spell.LevensteinDistance;
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
   
   

   public List<String> buildEnSuggestions(String wordToRespell){
       
        String[] suggestions = null;
        List<String> result = new ArrayList<>();
       
       try {
          
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSpellCheckerPathString()));
           SpellChecker spell = new SpellChecker(dir);
           spell.setStringDistance(new LevensteinDistance());
           suggestions = spell.suggestSimilar(wordToRespell, 2);
           System.out.println(suggestions.length +  " English suggestions for '" +  wordToRespell + "':");
           for(String suggestion : suggestions){
                System.out.println(" " + suggestion);
                result.add(suggestion);
            }
           
       } catch (IOException ex) {
           Logger.getLogger(Suggest.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return result;
   }
   
    public List<String> buildFrSuggestions(String wordToRespell){
       
        String[] suggestions = null;
        List<String> result = new ArrayList<>();
       
       try {
          
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSpellCheckerPathString()));
           SpellChecker spell = new SpellChecker(dir);
           spell.setStringDistance(new LevensteinDistance());
           suggestions = spell.suggestSimilar(wordToRespell, 2);
           System.out.println(suggestions.length +  " French suggestions for '" +  wordToRespell + "':");
           for(String suggestion : suggestions){
                System.out.println(" " + suggestion);
                result.add(suggestion);
            }
           
       } catch (IOException ex) {
           Logger.getLogger(Suggest.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return result;
   }
    
    
    public List<String> buildSpSuggestions(String wordToRespell){
       
        String[] suggestions = null;
        List<String> result = new ArrayList<>();
       
       try {
          
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSpellCheckerPathString()));
           SpellChecker spell = new SpellChecker(dir);
           spell.setStringDistance(new LevensteinDistance());
           suggestions = spell.suggestSimilar(wordToRespell, 2);
           System.out.println(suggestions.length +  " Spanish suggestions for '" +  wordToRespell + "':");
           for(String suggestion : suggestions){
                System.out.println(" " + suggestion);
                result.add(suggestion);
            }
           
       } catch (IOException ex) {
           Logger.getLogger(Suggest.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return result;
   }
   
   public String buildSuggestions(String wordToRespell){
       //List<String> resultList = new ArrayList<>();
       // Map<String,Integer> docs = new HashMap<>();
        List<String> docList = new ArrayList<>();
        
        List<String> englishDoc = buildEnSuggestions(wordToRespell);
        List<String> frenchDoc = buildFrSuggestions(wordToRespell);
        List<String> spanishDoc = buildSpSuggestions(wordToRespell);
        
        if(!englishDoc.isEmpty()){
             docList.add(englishDoc.get(0));
        }
        if(!frenchDoc.isEmpty()){
            docList.add(frenchDoc.get(0));
        }
        if(!spanishDoc.isEmpty()){
            docList.add(spanishDoc.get(0));
        }
        
       // String selectedText = max(docList); //get list with max size cause it likely has best hits
        
        return max(docList);
    }
    
    private String max(List<String> docs){ //get word with max size cause it likely has best suggestion
        
        String max = "";
        if(!docs.isEmpty()){
            max = docs.get(0);
            for(String text  : docs){

                if(text.length() > max.length() ){
                    max = text;
                }
            }
        }
        
        
        return max;
   }
  
   
}
