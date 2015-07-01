package com.xtayfjpk.elasticsearch.test.lucene.queryparser;

import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.xml.CorePlusExtensionsParser;
import org.apache.lucene.queryparser.xml.QueryTemplateManager;
import org.apache.lucene.search.Query;
import org.junit.Test;

import com.xtayfjpk.elasticsearch.test.lucene.LuceneUtils;

public class XmlQueryParserTest {
	
	@Test
	public void test() throws Exception {
		QueryTemplateManager manager = new QueryTemplateManager(XmlQueryParserTest.class.getResourceAsStream("/"));
		CorePlusExtensionsParser xmlParser = new CorePlusExtensionsParser("contents", new StandardAnalyzer());
		
		Properties formProperties = new Properties();
		formProperties.put("", "");
		org.w3c.dom.Document document = manager.getQueryAsDOM(formProperties);
		Query query = xmlParser.getQuery(document.getDocumentElement());
		LuceneUtils.outputQueryResult(query);
	}
}
