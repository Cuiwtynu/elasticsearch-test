package com.xtayfjpk.elasticsearch.test.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


@SuppressWarnings("deprecation")
public class Indexer {
	private IndexWriter writer;
	
	public Indexer(String indexDir, Analyzer analyzer) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
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
		writer.addDocument(document);
	}
	
	
	
	private Document getDocument(File file) throws Exception {
		Document document = new Document();
		document.add(new Field("contents", readFile(file), Field.Store.YES, Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
		
		Field filesize = new LongField("filesize", file.length(), Field.Store.YES);
		document.add(filesize);
		
		document.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		//如果Store.NO则表示该文档中的该字段不会进行非独存储，也就是调用doc.get(fieldName)是会直接返回null，但是会建立索引，可供查询
		document.add(new Field("fullpath", file.getCanonicalPath(), Field.Store.YES, Field.Index.ANALYZED));
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
	
	private String readFile(File file) throws Exception {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while((line=bufferedReader.readLine())!=null) {
			builder.append(line).append("\r");
		}
		bufferedReader.close();
		return builder.toString();
	}

	public static void main(String[] args) throws Exception {
		String indexDir = "lucene-index";
		String dataDir = "lucene_index_source";
		Analyzer analyzer = new StandardAnalyzer();
		Indexer indexer = new Indexer(indexDir, analyzer);
		int numDocs = indexer.index(dataDir, null);
		indexer.close();
		System.out.println("indexed doc count: " + numDocs);
		
	}
}
