package com.xtayfjpk.elasticsearch.test.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.xtayfjpk.elasticsearch.test.lucene.analyzer.SynonymAnalyzer;

public class LuceneUtils {
	public static final String indexDir = "lucene-index";
	
	public static IndexSearcher getSearcher(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
	public static IndexWriter getWriter(String indexDir) throws Exception {
		Analyzer analyzer = new SynonymAnalyzer();
		Indexer indexer = new Indexer(indexDir, analyzer);
		return indexer.getWriter();
	}
	
	public static void outputDocs(IndexSearcher searcher, TopDocs hits)	throws IOException {
		for(ScoreDoc doc : hits.scoreDocs) {
			Document document = searcher.doc(doc.doc);
			StringBuilder builder = new StringBuilder();
			builder.append("docId:").append(doc.doc).append(", ");
			builder.append("score:").append(doc.score).append(", ");
			builder.append("filesize:").append(document.get("filesize")).append(", ");
			builder.append("fullpath:").append(document.get("fullpath"));
			System.out.println(builder);
		}
	}
	
	public static void outputQueryResult(Query query) throws IOException {
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		TopDocs hits = searcher.search(query, 10);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
}
