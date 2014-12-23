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
import org.apache.lucene.analysis.es.SpanishAnalyzer;
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
import zuch.qualifier.SpellCheckerClear;
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

   static final Logger log = Logger.getLogger("zuch.service.ZSpellCheker");
   @Inject ZFileSystemUtils systemUtils;
   
   private static  final List<String> words = Arrays.asList("a","à");
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
   //@Lock(LockType.WRITE)
   @Asynchronous
   public void clearSpellCheckers(@Observes @SpellCheckerClear String value){
       clearEnSpellChecker();
       clearFrSpellChecker();
       clearSpSpellChecker();
   }
   
  // @Lock(LockType.WRITE)
   @Asynchronous
   public void buildEnSpellChecker(){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.EN_SPELLCHK)));
           Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.EN_INDEX)));
           try (IndexReader inReader = DirectoryReader.open(dir2)) {
                spell.indexDictionary(new LuceneDictionary(inReader, "contents"),config,true);
                
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   }
   
   //@Lock(LockType.WRITE)
   @Asynchronous
   public void clearEnSpellChecker(){
       log.warning("CLEAR EN SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.EN_SPELLCHK)));
           Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.EN_INDEX)));
           IndexReader inReader = DirectoryReader.open(dir2);
           try {
                spell.clearIndex();
               
               
           } finally {
                inReader.close();
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
  // @Lock(LockType.WRITE)
   @Asynchronous
   public void buildFrSpellChecker(){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.FR_SPELLCHK)));
           Analyzer analyser = new ZuchFrenchAnalyzer();
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.FR_INDEX)));
           IndexReader inReader = DirectoryReader.open(dir2);
           try {
                spell.indexDictionary(new LuceneDictionary(inReader, "contents"),config,true);
           } finally {
                inReader.close();
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   }
   
   
  // @Lock(LockType.WRITE)
   @Asynchronous
   public void clearFrSpellChecker(){
       
       log.warning("BUILD FR SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.FR_SPELLCHK)));
           Analyzer analyser = new ZuchFrenchAnalyzer();
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.FR_INDEX)));
           IndexReader inReader = DirectoryReader.open(dir2);
           try {
                spell.clearIndex();
                
           } finally {
                inReader.close();
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   }
   
   
   
   
  // @Lock(LockType.WRITE)
   @Asynchronous
   public void buildSpSpellChecker(){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.SP_SPELLCHK)));
           Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.SP_INDEX)));
           IndexReader inReader = DirectoryReader.open(dir2);
           try {
                spell.indexDictionary(new LuceneDictionary(inReader, "contents"),config,true);
           } finally {
                inReader.close();
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   }
   
   
  // @Lock(LockType.WRITE)
   @Asynchronous
   public void clearSpSpellChecker(){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getPathString(Folder.SP_SPELLCHK)));
           Analyzer analyser = new SpanishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getPathString(Folder.SP_INDEX)));
           IndexReader inReader = DirectoryReader.open(dir2);
           try {
                spell.clearIndex();
           } finally {
                inReader.close();
           }
           
           dir.close();
           dir2.close();
           long endTime = System.currentTimeMillis();
           System.out.println(" took " + (endTime-startTime) + " milliseconds");

       } catch (IOException ex) {
           Logger.getLogger(ZSpellChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   }
   
   
}
