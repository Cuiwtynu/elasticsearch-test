package com.xtayfjpk.elasticsearch.test.lucene;


import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.junit.Test;

public class QueryTest {
	
	/**
	 * 匹配所有文档
	 */
	@Test
	public void testMatchAllDocsQuery() throws Exception {
		Query query = new MatchAllDocsQuery();
		LuceneUtils.outputQueryResult(query);
	}
	
	/**
	 * TermQuery可用于NOT_ANALYZED与ANALYZED字段，term字段串为一个整体进行精确匹配(被检索的字段也必须含有一个以上一模一样的term)，
	 */
	@Test
	public void testTermQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TermQuery query = new TermQuery(new Term("contents", "document"));
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
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
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
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
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		BytesRef lowerTerm = new BytesRef("MoveBox.txt");
		BytesRef upperTerm = new BytesRef("z");
		TermRangeQuery query = new TermRangeQuery("filename", lowerTerm, upperTerm, true, true);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		NumericRangeQuery<Long> query = NumericRangeQuery.newLongRange("filesize", 0L, 500L, true, true);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testPrefixQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		Term prefix = new Term("filename", "Move");
		PrefixQuery query = new PrefixQuery(prefix);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testPhraseQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
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
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		Term term = new Term("contents", "roo?");
		//用于NOT_ANALYZED与ANALYZED字段都可行
		WildcardQuery query = new WildcardQuery(term);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testFuzzyQuery() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
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
	
	@Test
	public void testMultiPhraseQuery() throws Exception {
		MultiPhraseQuery query = new MultiPhraseQuery();
		query.add(new Term[]{new Term("contents", "room"), new Term("contents", "document")});
		query.add(new Term("contents", "index"));
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TopDocs hits = searcher.search(query, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	
	//TODO tieBreakerMultiplier有什么用，DisjunctionMaxQuery的运行原理
	@Test
	public void testDisjunctionMaxQuery() throws Exception {
		DisjunctionMaxQuery query = new DisjunctionMaxQuery(0.5f);
		TermQuery subquery = new TermQuery(new Term("contents", "javadoc"));
		query.add(subquery);
		
		query.add(new TermQuery(new Term("fullpath", "javadoc")));
		query.add(new TermQuery(new Term("contents", "frame")));
		System.out.println(query);
		LuceneUtils.outputQueryResult(query);
	}
	
	/**
	 * 跨度查询，在字段的某指定位置跨过多个term范围内检索匹配SpanQuery的文档
	 */
	@Test
	public void testSpanQuery() throws Exception {
		//-----------------------SpanFirstQuery----------------------------
		//从字段的起始位置至匹配SpanQuery之是的跨度如果小于slop则该文档匹配
		SpanTermQuery jdQuery = new SpanTermQuery(new Term("fullpath", "javadoc"));
		System.out.println(jdQuery);
		SpanFirstQuery query = new SpanFirstQuery(jdQuery, 10);
		LuceneUtils.outputQueryResult(query);
		
		//-----------------------SpanNearQuery----------------------------
		//在多个SpanQuery之间指定一个slop因子，如果某个文档在匹配多个SpanQuery过程中跨度小于slop则匹配
		SpanTermQuery testQuery = new SpanTermQuery(new Term("fullpath", "test"));
		SpanQuery[] clauses = new SpanQuery[]{jdQuery, testQuery};
		SpanNearQuery nearQuery = new SpanNearQuery(clauses, 4, false);
		LuceneUtils.outputQueryResult(nearQuery);
		
		//-----------------------SpanNotQuery----------------------------
		//SpanNotQuery会排除那些与SpanQuery对象相交叠的文档，例如：indexwriter位置在test与javadoc之间，出现了交叠，所该文档就会被排除
		//SpanTermQuery exclude = new SpanTermQuery(new Term("fullpath", "indexwriter"));
		//txt并没有在test与javadoc之间，所以不会被排除
		SpanTermQuery exclude = new SpanTermQuery(new Term("fullpath", "txt"));
		SpanNotQuery notQuery = new SpanNotQuery(nearQuery, exclude);
		LuceneUtils.outputQueryResult(notQuery);
		
		//-----------------------SpanNotQuery----------------------------
		//将多个SpanQuery匹配的文档联合起来
		SpanOrQuery orQuery = new SpanOrQuery(jdQuery, testQuery);
		LuceneUtils.outputQueryResult(orQuery);
	}

	
	/**
	 * ConstantScoreQuery将过滤器转换成查询以用于随后的搜索，生成的查询只对过滤器所包含的文档进行匹配，然后赋予它们与查询加权相等的评分
	 */
	@Test
	public void testConstantScoreQuery() throws Exception {
		PrefixFilter filter = new PrefixFilter(new Term("filename", "Move"));
		ConstantScoreQuery query = new ConstantScoreQuery(filter);
		query.setBoost(3.0f);
		LuceneUtils.outputQueryResult(query);
	}
	
	/**
	 * 自定义分数查询
	 */
	@Test
	public void testCustomScoreQuery() throws Exception {
		TermQuery subQuery = new TermQuery(new Term("contents", "document"));
		CustomScoreQuery query = new CustomScoreQuery(subQuery) {
			@Override
			protected CustomScoreProvider getCustomScoreProvider(AtomicReaderContext context) throws IOException {
				return new CustomScoreProvider(context) {
					@Override
					public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
						return 5.0f;//自定义分数值
					}
				};
			}
		};
		LuceneUtils.outputQueryResult(query);
	}
	
	@Test
	public void testFunctionQuery() throws Exception {
		//通过某字段的值作为分数
		ValueSource func = new LongFieldSource("filesize");
		FunctionQuery query = new FunctionQuery(func);
		LuceneUtils.outputQueryResult(query);
	}
	
	
	/**
	 * 在索引时必须设置了Field.TermVector参数getTermVector才有返回值，否则为null
	 */
	@Test
	public void testTermVector() throws Exception {
		IndexReader indexReader = LuceneUtils.getSearcher(LuceneUtils.indexDir).getIndexReader();
		Terms terms = indexReader.getTermVector(0, "contents");
		System.out.println(terms);
		System.out.println(terms.getDocCount());
		System.out.println(terms.getSumDocFreq());
		System.out.println(terms.getSumTotalTermFreq());
		TermsEnum termsEnum = terms.iterator(null);
		System.out.println(termsEnum);
		BytesRef term = null;
		while((term=termsEnum.next())!=null) {
			System.out.println(term.utf8ToString());
		}
	}
	
	/**
	 * 检查超时则会抛异常
	 */
	@Test
	public void testTimeLimitingCollator() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		Query query = new TermQuery(new Term("contents", "document"));
		TopScoreDocCollector collector = TopScoreDocCollector.create(10, true); 
		searcher.search(query, new TimeLimitingCollector(collector, Counter.newCounter(), 1));
		System.out.println(collector.getTotalHits());
		LuceneUtils.outputDocs(searcher, collector.topDocs());
	}
}	
