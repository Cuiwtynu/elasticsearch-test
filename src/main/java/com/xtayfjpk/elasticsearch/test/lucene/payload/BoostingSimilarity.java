package com.xtayfjpk.elasticsearch.test.lucene.payload;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

public class BoostingSimilarity extends DefaultSimilarity {
	
	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		System.out.println("payload:" + payload);
		if(payload!=null) {
			return PayloadHelper.decodeFloat(payload.bytes);
		}
		return 1.0f;
	}
}
