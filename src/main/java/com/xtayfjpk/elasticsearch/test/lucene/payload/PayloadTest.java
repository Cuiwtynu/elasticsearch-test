package com.xtayfjpk.elasticsearch.test.lucene.payload;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.junit.Before;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;
import com.xtayfjpk.elasticsearch.test.lucene.RAMIndexer;

public class PayloadTest {
	private RAMIndexer indexer;

	@Before
	public void before() throws Exception {
		Analyzer analyzer = new BulletinPayloadAnalyzer(5.0f, "warning");
		indexer = new RAMIndexer(analyzer);
		indexer.index("lucene_index_source", null);
		indexer.getWriter().close();
	}
	
	@Test
	public void test() throws Exception {
		IndexSearcher searcher = indexer.getIndexSearcher();
		
		
		Term term = new Term("contents", "warning");
		TermQuery termQuery = new TermQuery(term);
		TopDocs hits = searcher.search(termQuery, 10);
		LuceneUtils.outputDocs(searcher, hits);
		
		System.out.println(searcher.getSimilarity());
		searcher.setSimilarity(new BoostingSimilarity());
		
		PayloadFunction payloadFunction = new MaxPayloadFunction();
		PayloadTermQuery payloadTermQuery = new PayloadTermQuery(term, payloadFunction, true);
		hits = searcher.search(payloadTermQuery, 10);
		LuceneUtils.outputDocs(searcher, hits);
		
	}
}
