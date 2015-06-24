package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.filter.MaxFilesizeFilter;

public class FilterTest {
	
	@Test
	public void testTermRangeFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		//filename没有进行分词，不会自动转换为小写
		TermRangeFilter filter = new TermRangeFilter("filename", new BytesRef("I"), new BytesRef("M"), false, false);
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testNumericRangeFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		//filename没有进行分词，不会自动转换为小写
		NumericRangeFilter<Long> filter = NumericRangeFilter.newLongRange("filesize", 0L, 300L, true, true);
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	/**
	 * FieldCacheRangeFilter提供了另一种范围过滤选择。它所完成的过滤功能与TermRangeFilter和NumericRangeFilter加起来一样，但它的使用却是
	 * 基于Lucene的域缓存机制的。使用这个机制可以在某些情况下带来系统性能提升，因为所有的值都提前存储到内存中了。
	 */
	@Test
	public void testFieldCacheRangeFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		//filename没有进行分词，不会自动转换为小写
		FieldCacheRangeFilter<Long> filter = FieldCacheRangeFilter.newLongRange("filesize", 0L, 300L, true, true);
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	/**
	 * 使用FieldCacheTermsFilter时，检索字段必须有单值，不能出现多值(如分词后的多个term)
	 */
	@Test
	public void testFieldCacheTermsFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		//filename没有进行分词，不会自动转换为小写
		FieldCacheTermsFilter filter = new FieldCacheTermsFilter("filename", "MoveBox.txt", "IndexWriter addDocument's a javadoc .txt");
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	/**
	 * 将Query对象包装为Filter对象
	 */
	@Test
	public void testQueryWrapperFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TermQuery termQuery = new TermQuery(new Term("contents", "document"));
		QueryWrapperFilter filter = new QueryWrapperFilter(termQuery);
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testPrefixFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		PrefixFilter filter = new PrefixFilter(new Term("filename", "Move"));
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testCustomFilter() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		Filter filter = new MaxFilesizeFilter(400L);
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), filter, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
}
