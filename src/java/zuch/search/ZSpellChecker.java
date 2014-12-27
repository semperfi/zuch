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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import zuch.qualifier.AudioIndexed;
import zuch.qualifier.AudioRemovedFromIndex;

import zuch.util.Folder;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value=30,unit=TimeUnit.MINUTES)
public class ZSpellChecker {

   static final Logger log = Logger.getLogger(ZSpellChecker.class.getName());
   
   @Inject ZFileSystemUtils systemUtils;
   
   private static  final List<String> words = Arrays.asList("a","à");
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
   
   @Asynchronous
   public void OnAudioIndexed(@Observes @AudioIndexed String dummyPayload){
       buildEnSpellChecker();
       buildFrSpellChecker();
       
   }
   
   @Asynchronous
   public void OnAudioRemovedFromIndex(@Observes @AudioRemovedFromIndex String dummyPayload){
       buildEnSpellChecker();
       buildFrSpellChecker();
   }
   
   
  
   
   private void buildEnSpellChecker(){
       
       log.warning("BUILD EN SPELL CHECKER...");
       
       Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
       buildSpellChecker(Folder.EN_SPELLCHK, Folder.EN_INDEX, analyser);
       
      
   }
   
   
  
   
   private void buildFrSpellChecker(){
       
       log.warning("BUILD FR SPELL CHECKER...");
       Analyzer analyser = new FrenchAnalyzer(Version.LUCENE_4_9, stopWords);
       buildSpellChecker(Folder.FR_SPELLCHK, Folder.FR_INDEX, analyser);
    
   }
   
   
  
   
   
    private void buildSpellChecker(Folder spellFolder,Folder indexFolder,Analyzer analyzer){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(spellFolder)));
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
           SpellChecker spell = new SpellChecker(dir);
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(indexFolder)));
           try (IndexReader inReader = DirectoryReader.open(dir2)) {
                spell.clearIndex();
                spell.indexDictionary(new LuceneDictionary(inReader, "contents"),config,true);
                
           }
           
           dir.close();
           dir2.close();
          
       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   } 
   
   
  
   
}
