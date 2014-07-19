/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.search;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.fr.FrenchLightStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

/**
 *
 * @author florent
 */
public class ZuchFrenchAnalyzer extends StopwordAnalyzerBase{
    
    /** Tokens longer than this length are discarded. Defaults to 50 chars. */
    public int maxTokenLength = 50;
    
    private static  final List<String> words = Arrays.asList("a","à");
    
    private static final CharArraySet stopWords = 
           new CharArraySet(Version.LUCENE_4_9, words, true);
    
    public ZuchFrenchAnalyzer() {
        super(Version.LUCENE_4_9, stopWords);
    }

    @Override
    protected TokenStreamComponents createComponents(String string, Reader reader) {
        final Tokenizer source = new StandardTokenizer(matchVersion, reader);
       // source.setMaxTokenLength(maxTokenLength);

        TokenStream pipeline = source;
        pipeline = new StandardFilter(matchVersion, pipeline);
       // pipeline = new EnglishPossessiveFilter(matchVersion, pipeline);
        pipeline = new ASCIIFoldingFilter(pipeline);
        pipeline = new LowerCaseFilter(matchVersion, pipeline);
        pipeline = new StopFilter(matchVersion, pipeline, stopwords);
        pipeline = new FrenchLightStemFilter(pipeline);
       // pipeline = new PorterStemFilter(pipeline);
        return new TokenStreamComponents(source, pipeline);
    }

    

    
    
}
