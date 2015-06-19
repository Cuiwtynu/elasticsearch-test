package com.xtayfjpk.elasticsearch.test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

public class QueryParserTest {
	private static final String indexDir = "lucene-index";
	
	@Test
	public void testTermQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		//单个词在默认情况下如果不被识别为更长的其他查询类型表达式的一部分，那么它将被QueryParser解析为单个TermQuery
		QueryParser parser = new QueryParser("subject", a);
		Query query = parser.parse("computers");
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
	
	/**
	 * 针对文本和日期的范围查询采用的是用括号表示：表示范围时，"["表示边界包含在内，"{"表示边界排除在外，两个范围值必须用TO进行连接，"TO"必须是大写
	 * 对于非日期范围的查询，Lucene会在用户输入查询范围后将查询边界转换为小写字母形式，除非程序调用了QueryParser.setLowercaseExpandedTerms(false)方法
	 * 这样的话程序就不会对输入的文本进行分析。如果查询范围的起点或终点之间不包含空格，那它们必须用双引号括起来，否则程序会解析失败
	 */
	@Test
	public void testTermRangeQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("subject", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("[Q TO V");
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
	
	/**
	 * 如果某项中包含了一个星号或问号，该项就会被看作是通配符查询对象，而当查询项只在末尾有一个星号时，QueryParser类会将它做成为前缀查询对象。
	 * 不管是前缀查询还是通配符查询，其对象都会被转换为小写字母形式
	 */
	@Test
	public void testPrefixAndWildcardQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("subject", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("Lo?ve*");
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
	
	/**
	 * 查询语句中用双引号括起来的项可以用来创建一个PhraseQuery，引号之间的文本将被分析，分析的结果与使用的分词器有关
	 */
	@Test
	public void testPhraseQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setPhraseSlop(1);//指定phrase slop为1
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("\"the room contains*\"~1");//~1指定phrase slop为1
		System.out.println(query.getClass());
		System.out.println(query.toString());
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	/**
	 * 波浪符(~)会针对下在处理的项来创建模糊查询
	 * 需要注意的是，波浪符还可以用于指定松散短语查询，但具体环境各不相同。双引号表示短语查询，它并不能用于模糊查询。
	 * 你可以选择性指定一个浮点数，用来示所需的最小相似程度
	 */
	@Test
	public void testFuzzyQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("roo~1");//~1指定edit distance为1
		System.out.println(query.getClass());
		System.out.println(query.toString());
		IndexSearcher searcher = LuceneUtils.getSearcher(indexDir);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		LuceneUtils.outputDocs(searcher, hits);
	}
	
	@Test
	public void testMatchAllDocsQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("*:*");
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
	
	/**
	 * QueryParser使用分组后的文本类型的查询表达式来支持分组查询，使用括弧来形成子查询
	 * filename:(indeX OR test)，域的选择也必须用括弧括起来
	 */
	@Test
	public void testAggregationQuery() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("(room OR document) AND filename:(indeX OR test)");
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
	
	/**
	 * 为子查询设置加权
	 */
	@Test
	public void testSubQueryBoosting() throws Exception {
		Analyzer a = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", a);
		//parser.setLowercaseExpandedTerms(false);//禁止将查询语句转换为小写
		Query query = parser.parse("room^2.0");
		System.out.println(query.getBoost());
		System.out.println(query.getClass());
		System.out.println(query.toString());
	}
}
