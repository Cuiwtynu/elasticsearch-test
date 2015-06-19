package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

public class QueryTest {
	private static final String indexDir = "lucene-index";
	
	/**
	 * TermQuery可用于NOT_ANALYZED与ANALYZED字段，term字段串为一个整体进行精确匹配(被检索的字段也必须含有一个以上一模一样的term)，
	 */
	@Test
	public void testTermQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		TermQuery query = new TermQuery(new Term("contents", "document"));
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		Query query = parser.parse("button OR index");
		if(query instanceof BooleanQuery) {
			BooleanClause[] clauses = ((BooleanQuery)query).getClauses();
			if(clauses!=null) {
				for(BooleanClause clause : clauses) {
					System.out.println(clause.getQuery().getClass());
				}
			}
		}
		System.out.println(query.getClass());
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testExplain() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		Query query = parser.parse("button OR index");
		System.out.println(query.getClass());
		
		TopDocs hits = searcher.search(query, 10);
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document document = searcher.doc(scoreDoc.doc);
			System.out.println(document.get("filename"));
			Explanation explanation = searcher.explain(query, scoreDoc.doc);
			System.out.println(explanation);
			System.out.println("--------------------");
		}
		
	}
	
	@Test
	public void testTermRangeQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		BytesRef lowerTerm = new BytesRef("MoveBox.txt");
		BytesRef upperTerm = new BytesRef("z");
		TermRangeQuery query = new TermRangeQuery("filename", lowerTerm, upperTerm, true, true);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		NumericRangeQuery<Long> query = NumericRangeQuery.newLongRange("filesize", 0L, 500L, true, true);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testPrefixQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		Term prefix = new Term("filename", "Move");
		PrefixQuery query = new PrefixQuery(prefix);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testPhraseQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		//使用PhraseQuery的前提是建立索引时不调用设置ometTermFreqAndPositions选项
		PhraseQuery query = new PhraseQuery();
		Term term = new Term("contents", "room");
		query.add(term);
		term = new Term("contents", "contains");
		query.add(term);
		query.setSlop(0);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testWildcardQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		Term term = new Term("contents", "roo?");
		//用于NOT_ANALYZED与ANALYZED字段都可行
		WildcardQuery query = new WildcardQuery(term);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testFuzzyQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		Term term = new Term("filename", "MoveBos.ttt");
		FuzzyQuery query = new FuzzyQuery(term, 1);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testQueryToString() throws Exception {
		BooleanQuery query = new BooleanQuery();
		query.add(new FuzzyQuery(new Term("field", "kountry")), BooleanClause.Occur.MUST);
		query.add(new TermQuery(new Term("title", "western")), BooleanClause.Occur.SHOULD);
		System.out.println(query.toString("field"));
	}
}	
