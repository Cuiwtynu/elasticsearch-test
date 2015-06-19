package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerUtils {
	
	public static void outputTerms(Analyzer analyzer, String text) throws Exception {
		String fieldName = "testField";
		TokenStream stream = analyzer.tokenStream(fieldName, text);
		CharTermAttribute attribute = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while(stream.incrementToken()) {
			System.out.print("[" + attribute.toString() + "] ");
		}
		stream.close();
	}

	public static void main(String[] args) throws Exception {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		String text = "Adds room a document to this room index. If the room document contains room";
		outputTerms(analyzer, text);
	}
}
