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
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class ZSpellChecker {

   static final Logger log = Logger.getLogger("zuch.service.ZSpellCheker");
   @Inject ZFileSystemUtils systemUtils;
   
   private static  final List<String> words = Arrays.asList("a","à");
   private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
   
   
   @Lock(LockType.WRITE)
   public void buildSpellChecker(){
       
       log.warning("BUILD SPELL CHECKER...");
       
       try {
           Directory dir = NIOFSDirectory.open(new File(systemUtils.getEnSpellCheckerPathString()));
           Analyzer analyser = new EnglishAnalyzer(Version.LUCENE_4_9, stopWords);
           IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyser);
           SpellChecker spell = new SpellChecker(dir);
           long startTime = System.currentTimeMillis();
           
           Directory dir2 = FSDirectory.open(new File(systemUtils.getEnSearchIndexPathString()));
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
   
   
}
