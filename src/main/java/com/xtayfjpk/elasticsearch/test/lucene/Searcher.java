package com.xtayfjpk.elasticsearch.test.lucene;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

public class Searcher {
	
	public static void search(String indexDir, String q) throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		Query query = parser.parse(q);
		
		TopDocs hits = searcher.search(query, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}



	

	
	
	public static void main(String[] args) throws Exception {
		String indexDir = "lucene-index";
		String q = "super";
		search(indexDir, q);
	}
}
