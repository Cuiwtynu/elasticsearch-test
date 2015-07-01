package com.xtayfjpk.elasticsearch.test.lucene.score;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.junit.Before;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;
import com.xtayfjpk.elasticsearch.test.lucene.RAMIndexer;

public class ScoreTest {
	private RAMIndexer indexer;

	@Before
	public void before() throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
		indexer = new RAMIndexer(analyzer);
		indexer.index("lucene_index_source2", null);
		indexer.getWriter().close();
	}
	
	@Test
	public void testTermQueryScore() throws Exception {
		IndexSearcher searcher = indexer.getIndexSearcher();
		
		Term term = new Term("contents", "document");
		TermQuery termQuery = new TermQuery(term);
		//termQuery.setBoost(100.0f);
		TopDocs hits = searcher.search(termQuery, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testBooleanQueryScore() throws Exception {
		IndexSearcher searcher = indexer.getIndexSearcher();
		
		Term term1 = new Term("contents", "document");
		Term term2 = new Term("contents", "room");
		TermQuery termQuery1 = new TermQuery(term1);
		TermQuery termQuery2 = new TermQuery(term2);
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(new BooleanClause(termQuery1, Occur.SHOULD));
		booleanQuery.add(new BooleanClause(termQuery2, Occur.SHOULD));
		//termQuery.setBoost(100.0f);
		TopDocs hits = searcher.search(booleanQuery, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
}
