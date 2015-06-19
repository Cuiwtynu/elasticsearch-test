package com.xtayfjpk.elasticsearch.test.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LetterTokenizer;

import com.xtayfjpk.elasticsearch.test.lucene.filter.MetaphoneReplacementFilter;

public class MetaphoneReplacementAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		LetterTokenizer input = new LetterTokenizer(reader);
		MetaphoneReplacementFilter filter = new MetaphoneReplacementFilter(input);
		return new TokenStreamComponents(input, filter);
	}

}
