package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.analyzer.SynonymAnalyzer;

public class AnalyzerTest {
	private static final String indexDir = "lucene-index";
	
	@Test
	public void testAnalysis() throws Exception {
		Analyzer analyzer = new SynonymAnalyzer();
		String text = "Adds room a document to this room index. If the room document 100 contains room";
		AnalyzerUtils.outputTerms(analyzer, text);
	}
	
	
	@Test
	public void testSynonymAnalyzer() throws Exception {
		Analyzer a = new SynonymAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("documentation");//~1指定edit distance为1
		System.out.println(query.getClass());
		System.out.println(query.toString());
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
		
		TermQuery termQuery = new TermQuery(new Term("contents", "documentation"));
		hits = searcher.search(termQuery, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
}
