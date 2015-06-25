package com.xtayfjpk.elasticsearch.test.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.xtayfjpk.elasticsearch.test.lucene.payload.BulletinPayloadAnalyzer;

@SuppressWarnings("deprecation")
public class RAMIndexer {
	private IndexWriter writer;
	
	public RAMIndexer(Analyzer analyzer) throws Exception {
		Directory dir = new RAMDirectory();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_1_0, analyzer);
		writer = new IndexWriter(dir, conf);
		
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
	public int index(String dataDir, FileFilter filter) throws Exception {
		if(filter==null) {
			filter = new TextFileFilter();
		}
		File[] files = new File(dataDir).listFiles(filter);
		if(files!=null) {
			for(File file : files) {
				if(!file.isDirectory() && !file.isHidden() && file.exists()) {
					indexFile(file);
				}
			}
		}
		
		return writer.numDocs();
	}
	
	private void indexFile(File file) throws Exception {
		System.out.println("Indexing: " + file.getCanonicalPath());
		Document document = getDocument(file);
		Analyzer analyzer = writer.getAnalyzer();
		if(analyzer instanceof BulletinPayloadAnalyzer) {
			BulletinPayloadAnalyzer bulletinPayloadAnalyzer = (BulletinPayloadAnalyzer) analyzer;
			bulletinPayloadAnalyzer.setBulletin(file.getName().length()>15);
		}
		writer.addDocument(document);
	}
	
	
	
	private Document getDocument(File file) throws Exception {
		Document document = new Document();
		
		Field filesize = new LongField("filesize", file.length(), Field.Store.YES);
		document.add(filesize);
		
		//document.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		document.add(new StringField("filename", file.getName(), Field.Store.YES));
		//如果Store.NO则表示该文档中的该字段不会进行非独存储，也就是调用doc.get(fieldName)是会直接返回null，但是会建立索引，可供查询
		//document.add(new Field("fullpath", file.getCanonicalPath(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new TextField("fullpath", new StringReader(file.getCanonicalPath())));
		document.add(new TextField("contents", new FileReader(file)));
		
		
		
		Field lastModified = new LongField("lastModified", file.lastModified(), Field.Store.YES);
		document.add(lastModified);
		return document;
	}
	
	public IndexWriter getWriter() {
		return writer;
	}
	
	private static class TextFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().toLowerCase().endsWith(".txt");
		}
	}
	
	public IndexSearcher getIndexSearcher() throws Exception {
		return new IndexSearcher(DirectoryReader.open(writer.getDirectory()));
	}
}
