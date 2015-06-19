package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.xtayfjpk.elasticsearch.test.lucene.analyzer.MetaphoneReplacementAnalyzer;

public class AnalyzerUtils {
	
	public static void outputTerms(Analyzer analyzer, String text) throws Exception {
		String fieldName = "testField";
		TokenStream stream = analyzer.tokenStream(fieldName, text);
		CharTermAttribute attribute = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute positionIncrementAttribute = stream.addAttribute(PositionIncrementAttribute.class);
		OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
		TypeAttribute typeAttribute = stream.addAttribute(TypeAttribute.class);
		stream.reset();
		while(stream.incrementToken()) {
			System.out.println("[" + attribute.toString() + "] ");
			System.out.println("position increment:" + positionIncrementAttribute.getPositionIncrement());
			System.out.println("offset: [" + offsetAttribute.startOffset() + "," + offsetAttribute.endOffset() + "]");
			System.out.println("type: " + typeAttribute.type());
			System.out.println("--------------------------------------------");
		}
		stream.close();
	}

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new MetaphoneReplacementAnalyzer();
		String text = "Adds room a document to this room index. If the room document 100 contains room";
		outputTerms(analyzer, text);
	}
}
