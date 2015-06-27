package com.xtayfjpk.elasticsearch.test.lucene.highlight;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;

/**
 * 需要高亮的字段一定要存储
 * @author zj
 *
 */
public class HighLightTest {
	@Test
	public void test() throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TermQuery query = new TermQuery(new Term("contents", "document"));
		TopDocs hits = searcher.search(query, 10);
		QueryScorer queryScorer = new QueryScorer(query, "contents");
		Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 200);
		Formatter formatter = new SimpleHTMLFormatter("<span color='red'>", "</span>");
		Highlighter highlighter = new Highlighter(formatter, queryScorer);
		
		
		highlighter.setTextFragmenter(fragmenter);
		for(int i=0; i<hits.totalHits; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document document = searcher.doc(scoreDoc.doc);
			
			TokenStream tokenStream = TokenSources.getTokenStream(document, "contents", analyzer);
			String result = highlighter.getBestFragment(tokenStream, document.get("contents"));
			System.out.println(result);
		}
	}
	
	/**
	 * 使用FastVectorHighlighter时，字段建立索引时必须使用TermVector.WITH_POSITIONS_OFFSETS
	 * @throws Exception
	 */
	@Test
	public void testFastVectorHighlighter() throws Exception {
		FragListBuilder fragListBuilder = new SimpleFragListBuilder();
		FragmentsBuilder fragmentsBuilder = new SimpleFragmentsBuilder(
				BaseFragmentsBuilder.COLORED_PRE_TAGS, BaseFragmentsBuilder.COLORED_POST_TAGS);
		
		FastVectorHighlighter fastVectorHighlighter = new FastVectorHighlighter(true, true, fragListBuilder, fragmentsBuilder);
		
		TermQuery query = new TermQuery(new Term("contents", "document"));
		FieldQuery fieldQuery = fastVectorHighlighter.getFieldQuery(query);
		
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		TopDocs hits = searcher.search(query, 10);
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			String snippet = fastVectorHighlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "contents", 100);
			System.out.println(snippet);
		}
	}
}
