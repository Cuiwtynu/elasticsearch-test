package com.xtayfjpk.elasticsearch.test.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneUtils {
	
	public static IndexSearcher getSearcher(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
	public static void outputDocs(IndexSearcher searcher, TopDocs hits)	throws IOException {
		for(ScoreDoc doc : hits.scoreDocs) {
			Document document = searcher.doc(doc.doc);
			System.out.println(document.get("fullpath"));
		}
	}
}
