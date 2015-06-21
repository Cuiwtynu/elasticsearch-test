package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.sorter.SortingAtomicReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

public class AdvancedTest {
	
	@Test
	public void testFieldCache() throws Exception {
		Sort sort = new Sort(new SortField("filesize", SortField.Type.LONG)); // determines how the documents are sorted
		IndexReader indexReader = LuceneUtils.getSearcher(LuceneUtils.indexDir).getIndexReader();
		AtomicReader sortingReader = SortingAtomicReader.wrap(SlowCompositeReaderWrapper.wrap(indexReader), sort);
		
		Longs longs = FieldCache.DEFAULT.getLongs(sortingReader, "filesize", false);
		System.out.println(longs);
		System.out.println(longs.get(0));
	}
	
	@Test
	public void testSort() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		MatchAllDocsQuery query = new MatchAllDocsQuery();
		Sort sort = new Sort(new SortField("filesize", SortField.Type.LONG));
		TopDocs hits = searcher.search(query, 10, sort);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		QueryParser parser = new MultiFieldQueryParser(new String[]{"fullpath", "contents"},  new StandardAnalyzer());
		Query query = parser.parse("javadoc");
		System.out.println(query.getClass());
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TopDocs hits = searcher.search(query, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
}
