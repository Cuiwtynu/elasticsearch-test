package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

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

}
