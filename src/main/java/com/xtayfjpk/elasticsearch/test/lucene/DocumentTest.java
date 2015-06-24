package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.junit.Test;

/**
 * 
 * @author zj
 *
 */
public class DocumentTest {
	
	//暂时删除符合条件文档，其文档ID还被占着
	@Test
	public void testDelete() throws Exception {
		IndexWriter indexWriter = LuceneUtils.getWriter(LuceneUtils.indexDir);
		indexWriter.deleteDocuments(new Term("filename", "MoveBox.txt"));
		indexWriter.commit();
	}
	
	//彻底删除符合条件文件，当文档被彻底删除后，文档ID会释放，这样存活文档ID号又变为连续的了
	@Test
	public void testRealDelete() throws Exception {
		IndexWriter indexWriter = LuceneUtils.getWriter(LuceneUtils.indexDir);
		indexWriter.deleteDocuments(new Term("filename", "MoveBox.txt"));
		//彻底删除
		indexWriter.forceMergeDeletes();
		indexWriter.close();
	}
	
	//3.6版本后已不能恢复
	@Test
	public void testRecoverDoc() throws Exception {
	}
}
