package com.xtayfjpk.elasticsearch.test.lucene.payload;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class BulletinPayloadAnalyzer extends Analyzer {
	private float waringBoost;
	private String bulletinTerm;
	private boolean bulletin;
	
	public BulletinPayloadAnalyzer(float waringBoost, String bulletinTerm) {
		this.waringBoost = waringBoost;
		this.bulletinTerm = bulletinTerm;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		System.out.println("field name:" + fieldName);
		Tokenizer input = new StandardTokenizer(reader);
		BulletinPayloadFilter source = new BulletinPayloadFilter(input, waringBoost, bulletinTerm);
		source.setBulletin(bulletin);
		return new TokenStreamComponents(input, source);
	}
	
	
	
	public void setBulletin(boolean bulletin) {
		this.bulletin = bulletin;
	}
}
