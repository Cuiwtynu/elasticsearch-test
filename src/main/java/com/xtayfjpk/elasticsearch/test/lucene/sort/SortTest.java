package com.xtayfjpk.elasticsearch.test.lucene.sort;

import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;

public class SortTest {
	
	@Test
	public void test() throws Exception {
		IndexSearcher searcher = LuceneUtils.getSearcher(LuceneUtils.indexDir);
		Sort sort = new Sort(new SortField("filesize", new FilesizeComparatorSource()));
		TopDocs hits = searcher.search(new MatchAllDocsQuery(), 10, sort);
		LuceneUtils.outputDocs(searcher, hits);
		
		ScoreDoc[] fieldDocs = hits.scoreDocs;
		if(fieldDocs!=null) {
			for(int i=0; i< fieldDocs.length; i++) {
				FieldDoc fieldDoc = (FieldDoc) fieldDocs[i];
				for(Object field : fieldDoc.fields) {
					System.out.println(field);
				}
			}
		}
	}
}
