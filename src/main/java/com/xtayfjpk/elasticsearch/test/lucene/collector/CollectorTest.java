package com.xtayfjpk.elasticsearch.test.lucene.collector;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;

public class CollectorTest {
	
	@Test
	public void test() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		searcher.search(new MatchAllDocsQuery(), new TestCollector());
	}
}
