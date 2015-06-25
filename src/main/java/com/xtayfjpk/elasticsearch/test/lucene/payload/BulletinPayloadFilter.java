package com.xtayfjpk.elasticsearch.test.lucene.payload;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

public class BulletinPayloadFilter extends TokenFilter {
	private CharTermAttribute termAttr;
	private PayloadAttribute payloadAttr;
	private boolean bulletin;
	private BytesRef boostPayload;
	
	private String bulletinTerm;

	public BulletinPayloadFilter(TokenStream input, float waringBoost, String bulletinTerm) {
		super(input);
		this.payloadAttr = this.addAttribute(PayloadAttribute.class);
		this.termAttr = this.addAttribute(CharTermAttribute.class);
		this.boostPayload = new BytesRef(PayloadHelper.encodeFloat(waringBoost));
		this.bulletinTerm = bulletinTerm;
	}

	public void setBulletin(boolean bulletin) {
		this.bulletin = bulletin;
	}
	
	@Override
	public boolean incrementToken() throws IOException {
		if(input.incrementToken()) {
			String term = new String(termAttr.buffer(), 0, termAttr.length());
			if(bulletin && term.equals(bulletinTerm)) {
				payloadAttr.setPayload(boostPayload);
			} else {
				payloadAttr.setPayload(null);
			}
			
			return true;
		}
		return false;
	}

	
}
