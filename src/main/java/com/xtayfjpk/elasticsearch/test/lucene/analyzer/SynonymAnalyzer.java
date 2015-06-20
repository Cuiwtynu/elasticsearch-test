package com.xtayfjpk.elasticsearch.test.lucene.analyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.CharsRef;

public class SynonymAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

		
		
		StandardTokenizer source = new StandardTokenizer(reader);
		SynonymMap.Builder builder = new SynonymMap.Builder(true);
		SynonymMap synonyms = null;
		try {
			CharsRef input = new CharsRef("document");
			CharsRef output = new CharsRef("doc");
			CharsRef output2 = new CharsRef("documentation");
			builder.add(input, output, true);
			builder.add(input, output2, true);
			synonyms = builder.build();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CharArraySet stopWords = new CharArraySet(0, true);
		stopWords.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		StopFilter stopFilter = new StopFilter(
				new LowerCaseFilter(
						new StandardFilter(source)), stopWords);
		SynonymFilter result = new SynonymFilter(stopFilter, synonyms, true);
		return new TokenStreamComponents(source, result);
	}

}
